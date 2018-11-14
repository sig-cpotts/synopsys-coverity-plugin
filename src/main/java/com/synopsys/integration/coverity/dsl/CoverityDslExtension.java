/**
 * synopsys-coverity
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

package com.synopsys.integration.coverity.dsl;

import java.util.List;

import com.synopsys.integration.coverity.common.BuildStatus;
import com.synopsys.integration.coverity.common.CoverityAnalysisType;
import com.synopsys.integration.coverity.common.CoverityRunConfiguration;
import com.synopsys.integration.coverity.common.OnCommandFailure;
import com.synopsys.integration.coverity.common.RepeatableCommand;
import com.synopsys.integration.coverity.freestyle.CoverityBuildStep;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class CoverityDslExtension extends ContextExtensionPoint {
    @DslExtensionMethod(context = StepContext.class)
    public Object coverity(final String coverityToolName, final String onCommandFailure, final List<String> commands, final String buildStatusForIssues, final String projectName, final String streamName,
        final String viewName, final String coverityRunConfiguration, final String coverityAnalysisType, final String buildCommand, final String changeSetExclusionPatterns, final String changeSetInclusionPatterns,
        final Boolean commandArguments, final String covBuildArguments, final String covAnalyzeArguments, final String covRunDesktopArguments, final String covCommitDefectsArguments, final Boolean checkForIssuesInView,
        final Boolean configureChangeSetPatterns) {
        return new CoverityBuildStep(coverityToolName, OnCommandFailure.valueOf(onCommandFailure), stringsToCommands(commands), BuildStatus.valueOf(buildStatusForIssues), projectName, streamName,
            CoverityRunConfiguration.valueOf(coverityRunConfiguration), CoverityAnalysisType.valueOf(coverityAnalysisType), buildCommand, viewName, changeSetExclusionPatterns, changeSetInclusionPatterns, commandArguments, covBuildArguments,
            covAnalyzeArguments, covRunDesktopArguments, covCommitDefectsArguments, checkForIssuesInView,
            configureChangeSetPatterns);
    }

    private RepeatableCommand[] stringsToCommands(final List<String> commands) {
        if (null == commands) {
            return null;
        }
        return (RepeatableCommand[]) commands.stream().map(RepeatableCommand::new).toArray();
    }

}
