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

import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;

public class DefaultSafeExecutor implements SafeExecutor {

    private List<SafeExecutionListener> listeners = new ArrayList<>();

    @Override
    public <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters) {
        try {
            R result = executable.execute(context, client, element, parameters);
            afterExecution(context, executable, client, element, parameters);
            return result;
        } catch (Exception e) {
            /* NPE will be logged */
            if (e instanceof NullPointerException) {
                context.getErrorHandler().logError("NPE occurred", e);
            }
            if (element instanceof AbstractEKubeElement) {
                AbstractEKubeElement ae = (AbstractEKubeElement) element;
                ae.setErrorMessage(e.getMessage());
                ae.setLocked(true);
            } else {
                context.getErrorHandler().logError("Kube element not wellknown, cannot set error and lock to :" + element, e);
            }
            return null;
        }
    }

    private <E extends EKubeElement, C, D, R> void afterExecution(EKubeContext context, SafeExecutable<E, C, R> executable, C client, E element,
            ExecutionParameters params) {
        for (SafeExecutionListener listener : listeners) {
            listener.afterExecute(context, executable, element, client, params);
        }
    }

    @Override
    public void add(SafeExecutionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(SafeExecutionListener listener) {
        listeners.remove(listener);
    }
}
