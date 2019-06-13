/**
 * synopsys-coverity
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.jenkins.coverity.steps;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.synopsys.integration.jenkins.coverity.ChangeSetFilter;
import com.synopsys.integration.jenkins.coverity.extensions.ConfigureChangeSetPatterns;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.rest.RestConstants;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;

public class ProcessChangeSet extends BaseCoverityStep {
    public ProcessChangeSet(final Node node, final TaskListener listener, final EnvVars envVars, final FilePath workspace, final Run run) {
        super(node, listener, envVars, workspace, run);
    }

    public List<String> computeChangeSet(final List<ChangeLogSet<?>> changeLogSets, final ConfigureChangeSetPatterns configureChangeSetPatterns) {
        this.initializeJenkinsCoverityLogger();

        final ChangeSetFilter changeSetFilter;
        if (configureChangeSetPatterns == null) {
            changeSetFilter = ChangeSetFilter.createAcceptAllFilter();
            logger.alwaysLog("-- No change set inclusion or exclusion patterns set");
        } else {
            changeSetFilter = configureChangeSetPatterns.createChangeSetFilter();
            logger.alwaysLog("-- Change set inclusion patterns: " + configureChangeSetPatterns.getChangeSetInclusionPatterns());
            logger.alwaysLog("-- Change set exclusion patterns: " + configureChangeSetPatterns.getChangeSetExclusionPatterns());
        }

        final List<String> changeSet = changeLogSets.stream()
                                           .filter(changeLogSet -> !changeLogSet.isEmptySet())
                                           .flatMap(this::toEntries)
                                           .peek(this::logEntry)
                                           .flatMap(this::toAffectedFiles)
                                           .filter(changeSetFilter::shouldInclude)
                                           .map(ChangeLogSet.AffectedFile::getPath)
                                           .collect(Collectors.toList());
        logger.alwaysLog("Added " + changeSet.size() + " files to $CHANGE_SET");

        return changeSet;
    }

    private Stream<? extends ChangeLogSet.Entry> toEntries(final ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet) {
        return StreamSupport.stream(changeLogSet.spliterator(), false);
    }

    private Stream<? extends ChangeLogSet.AffectedFile> toAffectedFiles(final ChangeLogSet.Entry entry) {
        return entry.getAffectedFiles().stream();
    }

    private void logEntry(final ChangeLogSet.Entry entry) {
        if (logger.getLogLevel().isLoggable(LogLevel.DEBUG)) {
            final Date date = new Date(entry.getTimestamp());
            logger.debug(String.format("Commit %s by %s on %s: %s", entry.getCommitId(), entry.getAuthor(), RestConstants.formatDate(date), entry.getMsg()));
        }
    }
}
