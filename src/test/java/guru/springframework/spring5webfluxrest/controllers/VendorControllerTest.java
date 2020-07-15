package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

class VendorControllerTest {

    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @BeforeEach
    void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    void getList() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Vend1").lastName("Vend1").build(),
                        Vendor.builder().firstName("Vend2").lastName("Vend2").build()));
        webTestClient.get().uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName("Vend1").lastName("Vend1").build()));
        webTestClient.get().uri("/api/v1/vendors/someid")
                .exchange()
                .expectBodyList(Vendor.class);
    }

    @Test
    void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToSave = Mono.just(Vendor.builder().firstName("Fred").lastName("Flintstone").build());

        webTestClient.post()
                .uri("/api/v1/vendors/")
                .body(vendorMonoToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Fred").lastName("Flintstone").build());

        webTestClient.put()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testPatchWithChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder()
                .firstName("Fred").lastName("Flintstone").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchNoChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(vendorRepository).save(any());
    }
}