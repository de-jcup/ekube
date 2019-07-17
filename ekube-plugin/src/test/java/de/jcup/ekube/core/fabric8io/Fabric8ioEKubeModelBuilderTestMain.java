package de.jcup.ekube.core.fabric8io;

import de.jcup.ekube.core.DefaultEKubeConfiguration;
import de.jcup.ekube.core.DefaultEKubeContext;
import de.jcup.ekube.core.ErrorHandler;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelToStringDumpConverter;

public class Fabric8ioEKubeModelBuilderTestMain {

    public static void main(String[] args) {
        DefaultEKubeConfiguration configuration = new DefaultEKubeConfiguration();

        DefaultEKubeContext context = new DefaultEKubeContext(new ErrorHandler() {

            @Override
            public void logError(String message, Exception e) {
                System.err.println(message);
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }, configuration);
        Fabric8ioConfigurationUpdater updater = new Fabric8ioConfigurationUpdater();
        updater.update(context); // loads kube config from file system...

        long time1 = System.currentTimeMillis();
        EKubeModel model = new Fabric8ioEKubeModelBuilder().build(context);
        long time2 = System.currentTimeMillis();

        System.out.println(new EKubeModelToStringDumpConverter().convert(model));

        System.out.println("time:" + (time2 - time1));
    }

}
