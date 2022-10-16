//package cc.towerdefence.api.serverorchestratorjava.service;
//
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.openapi.apis.CoreV1Api;
//import io.kubernetes.client.openapi.models.V1PodList;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//@Service
//@RequiredArgsConstructor
//public class VelocityServerService {
//    private final ApiClient apiClient;
//
//    @PostConstruct
//    @SneakyThrows
//    private void createService() {
//
//        CoreV1Api api = new CoreV1Api();
//
//
//        V1PodList podList = api.listNamespacedPod(
//                "towerdefence", null, null, null,
//                null, "", null, null, null,
//                null, null
//        );
//
//        System.out.println(podList);
//    }
//}
