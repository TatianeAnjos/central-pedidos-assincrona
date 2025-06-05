package com.tatiane.centralpedidos.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/central-pedidos/metricas")
@Slf4j
public class MetricasController {

    private final MeterRegistry meterRegistry;

//    public HealthMetricTestController(MeterRegistry meterRegistry) {
//        this.meterRegistry = meterRegistry;
//    }

    @GetMapping("/simular-processamento")
    public String simularProcessamento() {
        meterRegistry.counter("eventos_processados_sucesso").increment();
        return "Evento simulado!";
    }
}