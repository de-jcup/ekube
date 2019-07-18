/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.core;

public class DefaultEKubeContext implements EKubeContext {

    private ErrorHandler errorHandler;
    private EKubeConfiguration configuration;
    private EKubeProgressHandler progressHandler;
    private SafeExecutor executor;

    public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration) {
        this(errorHandler, configuration, null);
    }

    public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration, EKubeProgressHandler progressHandler) {
        this.errorHandler = errorHandler;
        this.configuration = configuration;
        if (progressHandler == null) {
            this.progressHandler = new NullProgressHandler();
        } else {
            this.progressHandler = progressHandler;
        }
        this.executor = new DefaultSafeExecutor();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public EKubeConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public EKubeProgressHandler getProgressHandler() {
        return progressHandler;
    }

    @Override
    public SafeExecutor getExecutor() {
        return executor;
    }

}
