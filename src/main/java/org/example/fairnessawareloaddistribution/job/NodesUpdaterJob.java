package org.example.fairnessawareloaddistribution.job;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.example.fairnessawareloaddistribution.config.FairnessConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ankushs
 */
@Component
@Slf4j
public class NodesUpdaterJob {

    private final FairnessConfig fairnessConfig;

    @Value("${kubernetes.namespace}")
    private String namespace;

    @Value("${kubernetes.deploymentName}")
    private String deploymentName;

    private KubernetesClient client;

    public NodesUpdaterJob(FairnessConfig fairnessConfig) {
        this.fairnessConfig = fairnessConfig;
    }

    @PostConstruct
    public void init() {
        log.info("Kubernetes namespace: {}", namespace);
        log.info("Kubernetes deploymentName: {}", deploymentName);
        client = new DefaultKubernetesClient();
        // Watch for changes to pods in the deployment
        client.pods().inNamespace(namespace).withLabel("app", deploymentName).watch(new Watcher<>() {
            @Override
            public void eventReceived(Watcher.Action action, Pod pod) {
                log.info("Event received for Pod {}: {}", pod.getMetadata().getName(), action);
                switch (action) {
                    case ADDED:
                        fairnessConfig.addNode(pod.getMetadata().getName());
                        log.info("Fairness config: {}", fairnessConfig);
                        break;
                    case DELETED:
                        fairnessConfig.removeNode(pod.getMetadata().getName());
                        log.info("Fairness config: {}", fairnessConfig);
                        break;
                    default:
                        log.warn("Unknown action: {}", action);
                }
            }

            @Override
            public void onClose(WatcherException e) {
                if (e != null) {
                    log.error("Watcher closed with exception", e);
                }
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        client.close();
    }
}
