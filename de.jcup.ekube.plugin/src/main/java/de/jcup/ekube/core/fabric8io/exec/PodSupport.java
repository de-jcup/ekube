package de.jcup.ekube.core.fabric8io.exec;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.PodConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodConditionElement;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class PodSupport extends AbstractSupport {

    public PodSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddPodsFromNamespaceExecutable addPodsFromNamespaceExecutable = new AddPodsFromNamespaceExecutable();
    private AddPodChildrenExecutable addPodChildrenExecutable = new AddPodChildrenExecutable();
    private FetchPodLogsExecutable fetchLogsExecutable = new FetchPodLogsExecutable();
    private PodConditionInfoBuilder conditionInfoBuilder = new PodConditionInfoBuilder();
    private UpdatePodsExecutable updateStatus = new UpdatePodsExecutable();

    public void addPodsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addPodsFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addPodsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addPodsFromNamespaceExecutable, namespaceContainer, client);
    }

    public void addPodChildren(EKubeContext context, KubernetesClient client, PodContainer namespaceContainer, Pod pod) {
        context.getExecutor().execute(context, addPodChildrenExecutable, namespaceContainer, client, new ExecutionParameters().set(Pod.class, pod));
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
        context.getExecutor().execute(context, updateStatus, podContainer, client, parameters);
    }

    private class UpdatePodsExecutable implements Fabric8ioSafeExecutable<PodContainer, Void> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
            Pod pod = parameters.get(Pod.class);
            PodStatus status = pod.getStatus();
            List<ContainerStatus> containerStatuses = status.getContainerStatuses();
            int ready = 0;
            int count = 0;
            for (ContainerStatus scs : containerStatuses) {
                count++;
                if (Boolean.TRUE.equals(scs.getReady())) {
                    ready++;
                }
            }
            podContainer.setStatus("Ready: " + ready + "/" + count);
            return null;
        }

    }

    private class AddPodsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespaceName = namespaceContainer.getName();
            PodList podList = client.pods().inNamespace(namespaceName).list();
            PodsContainer podsContainer = namespaceContainer.fetchPodsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            podsContainer.setAction(x);

            podsContainer.startOrphanCheck(parameters);
            for (Pod pod : podList.getItems()) {
                PodContainer newElement = new PodContainer(pod.getMetadata().getUid(), pod);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                PodContainer podContainer = podsContainer.AddOrReuseExisting(newElement);
                updateStatus(context, client, podContainer, new ExecutionParameters().set(Pod.class, pod));

                podContainer.setName(pod.getMetadata().getName());
                addPodChildren(context, client, podContainer, pod);
            }
            podsContainer.removeOrphans();
            return null;
        }

    }
    private class FetchPodLogsExecutable implements Fabric8ioSafeExecutable<PodContainer,File> {

        @Override
        public File execute(EKubeContext context, KubernetesClient client, PodContainer element, ExecutionParameters parameters) {
            Object obj = element.getTechnicalObject();
            if (! (obj instanceof Pod)){
                return null;
            }
            Pod pod = (Pod) obj;
            Integer logSize= parameters.get(Integer.class);
            if (logSize==null){
                logSize=100;
            }
            String podName = pod.getMetadata().getName();
            try{
                File file = File.createTempFile("ekube_log_"+podName+"_", ".log");
                file.deleteOnExit();
                
                FileOutputStream fos = new FileOutputStream(file);
                client.pods().inNamespace(pod.getMetadata().getNamespace()).withName(podName).tailingLines(logSize).watchLog(fos);
                return file;
            }catch(Exception e){
                context.getErrorHandler().logError("Was not able to fetch logs from pod:"+podName, e);
                return null;
            }
            
        }
        
    }
    private class AddPodChildrenExecutable implements Fabric8ioSafeExecutableNoData<PodContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
            Pod pod = parameters.get(Pod.class);

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<PodContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this, EKubeActionIdentifer.REFRESH_CHILDREN,
                    context, client, podContainer);
            podContainer.setAction(x);

            Fabric8ioGenericExecutionAction<PodContainer, File> getLogsAction = new Fabric8ioGenericExecutionAction<>(fetchLogsExecutable, EKubeActionIdentifer.FETCH_LOGS,
                    context, client, podContainer);
            podContainer.setAction(getLogsAction);
            
            podContainer.clearConditions();
            List<PodCondition> conditions = pod.getStatus().getConditions();
            ConditionInfoStatus status = ConditionInfoUtil.createStatus();
            for (PodCondition condition : conditions) {
                PodConditionElement element = new PodConditionElement(pod.getMetadata().getUid() + "#" + condition.getType(), condition);
                element.setName(condition.getType());
                ConditionInfo buildInfo = conditionInfoBuilder.buildInfo(condition);
                status.handle(buildInfo);
                element.setInfo(buildInfo);
                podContainer.add(element);
            }
            status.handleErrors(podContainer);
            return null;
        }

    }

}
