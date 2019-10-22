package de.jcup.ekube.core.fabric8io.exec.pod;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.PodConditionElement;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddPodActionsAndChildrenExecutable implements Fabric8ioSafeExecutableNoData<PodContainer> {

    /**
     * 
     */
    private final PodSupport podSupport;

    /**
     * @param podSupport
     */
    AddPodActionsAndChildrenExecutable(PodSupport podSupport) {
        this.podSupport = podSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
        Pod pod = parameters.get(Pod.class);

        Fabric8ioGenericExecutionAction<PodContainer, Void> refreshAction = new Fabric8ioGenericExecutionAction<>(this, EKubeActionIdentifer.REFRESH_CHILDREN,
                context, client, podContainer);
        Fabric8ioGenericExecutionAction<PodContainer, File> getLogsAction = new Fabric8ioGenericExecutionAction<>(this.podSupport.fetchLogsExecutable, EKubeActionIdentifer.FETCH_LOGS,
                context, client, podContainer);
        Fabric8ioGenericExecutionAction<PodContainer, Void> interactiveShellAction = new Fabric8ioGenericExecutionAction<>(this.podSupport.interactiveShellExecutable, EKubeActionIdentifer.INTERACTIVE_SHELL,
                context, client, podContainer);
        Fabric8ioGenericExecutionAction<PodContainer, Void> interactiveLogViewerAction = new Fabric8ioGenericExecutionAction<>(this.podSupport.interactiveLogViewerExecutable, EKubeActionIdentifer.INTERACTIVE_LOGVIEWER,
                context, client, podContainer);

        podContainer.register(refreshAction);
        podContainer.register(getLogsAction);
        podContainer.register(interactiveShellAction);
        podContainer.register(interactiveLogViewerAction);
        podContainer.clearConditions();
        
        List<PodCondition> conditions = pod.getStatus().getConditions();
        ConditionInfoStatus status = ConditionInfoUtil.createStatus();
        for (PodCondition condition : conditions) {
            ConditionInfo buildInfo = this.podSupport.conditionInfoBuilder.buildInfo(condition);
            status.handle(buildInfo);

            PodConditionElement element = new PodConditionElement(pod.getMetadata().getUid() + "#" + condition.getType(), condition);
            element.setName(condition.getType());
            element.setInfo(buildInfo);
            podContainer.add(element);
        }
        status.handleErrors(podContainer);
        
        
        
        
        return null;
    }

}