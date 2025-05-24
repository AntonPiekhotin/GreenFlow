package org.greenflow.order.config;

import org.greenflow.order.model.entity.Price;
import org.greenflow.order.model.entity.Service;
import org.greenflow.order.output.persistent.PriceRepository;
import org.greenflow.order.output.persistent.ServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ServiceRepository serviceRepo;
    private final PriceRepository priceRepo;

    public DataInitializer(ServiceRepository serviceRepo, PriceRepository priceRepo) {
        this.serviceRepo = serviceRepo;
        this.priceRepo = priceRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (serviceRepo.count() > 0) {
            return;
        }

        List<Service> services = List.of(
                createService("Lawn Mowing", "m²", new BigDecimal("5")),
                createService("Hedge Trimming", "pcs", new BigDecimal("30")),
                createService("Tree Pest Control", "hr", new BigDecimal("200")),
                createService("Soil Aeration", "m²", new BigDecimal("10")),
                createService("Fertilization", "m²", new BigDecimal("15")),
                createService("Weeding", "m²", new BigDecimal("8")),
                createService("Pruning", "hr", new BigDecimal("40")),
                createService("Irrigation System Setup", "unit", new BigDecimal("150")),
                createService("Leaf Removal", "m²", new BigDecimal("12"))
        );

        serviceRepo.saveAll(services);

        for (Service svc : services) {
            Price price = new Price();
            price.setService(svc);
            price.setRate(svc.getPrices().getFirst().getRate());
            price.setSeasonMultiplier(BigDecimal.ONE);
            priceRepo.save(price);
        }
    }

    private Service createService(String name, String unit, BigDecimal baseRate) {
        Service svc = new Service();
        svc.setName(name);
        svc.setUnit(unit);
        Price initial = new Price();
        initial.setService(svc);
        initial.setRate(baseRate);
        initial.setSeasonMultiplier(BigDecimal.ONE);
        svc.setPrices(List.of(initial));
        return svc;
    }
}