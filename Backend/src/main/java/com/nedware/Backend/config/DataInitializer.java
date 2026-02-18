package com.nedware.Backend.config;

import com.nedware.Backend.domain.Dish;
import com.nedware.Backend.domain.Restaurant;
import com.nedware.Backend.repository.DishRepository;
import com.nedware.Backend.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    public DataInitializer(RestaurantRepository restaurantRepository, DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (restaurantRepository.count() == 0) {
            // Create Restaurant 1
            Restaurant r1 = new Restaurant();
            r1.setName("Le Chef Gourmet");
            r1.setDescription("Experiência gastronômica francesa autêntica.");
            r1.setAddress("Rua das Flores, 123");
            r1.setLatitude(-23.5505);
            r1.setLongitude(-46.6333);
            r1.setRating(4.8);
            r1.setCoverImage("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4");

            Restaurant savedR1 = restaurantRepository.save(r1);

            Dish d1 = new Dish();
            d1.setName("Filé ao Molho Madeira");
            d1.setPrice(new BigDecimal("85.00"));
            d1.setDescription("Filé mignon grelhado com molho madeira e batatas gratinadas.");
            d1.setPhotoUrl("https://images.unsplash.com/photo-1600891964092-4316c288032e");
            d1.setAverageRating(4.9);
            d1.setRestaurant(savedR1);
            dishRepository.save(d1);

            Dish d2 = new Dish();
            d2.setName("Petit Gâteau");
            d2.setPrice(new BigDecimal("32.90"));
            d2.setDescription("Bolo de chocolate com recheio cremoso e sorvete de baunilha.");
            d2.setPhotoUrl("https://images.unsplash.com/photo-1624353365286-3f8d62daad51");
            d2.setAverageRating(4.7);
            d2.setRestaurant(savedR1);
            dishRepository.save(d2);

            // Create Restaurant 2
            Restaurant r2 = new Restaurant();
            r2.setName("Sushi House");
            r2.setDescription("O melhor da culinária japonesa.");
            r2.setAddress("Av. Paulista, 900");
            r2.setLatitude(-23.5614);
            r2.setLongitude(-46.6565);
            r2.setRating(4.5);
            r2.setCoverImage("https://images.unsplash.com/photo-1579871494447-9811cf80d66c");

            Restaurant savedR2 = restaurantRepository.save(r2);

            Dish d3 = new Dish();
            d3.setName("Combo Sushis Variados");
            d3.setPrice(new BigDecimal("120.00"));
            d3.setDescription("Seleção de 40 peças de sushis e sashimis frescos.");
            d3.setPhotoUrl("https://images.unsplash.com/photo-1553621042-f6e147245754");
            d3.setAverageRating(5.0);
            d3.setRestaurant(savedR2);
            dishRepository.save(d3);

            System.out.println("Data Initialized with 2 Restaurants and 3 Dishes.");
        }
    }
}

