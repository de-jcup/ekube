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

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutor {

    default <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client) {
        return execute(context, executable, element, client, new ExecutionParameters());
    }

    /**
     * Executes executable. 
     * @param context
     * @param executable
     * @param element
     * @param client
     * @param parameters
     * @return result or <code>null</code>
     */
    <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters);

    public void add(SafeExecutionListener listener);

    public void remove(SafeExecutionListener listener);
}