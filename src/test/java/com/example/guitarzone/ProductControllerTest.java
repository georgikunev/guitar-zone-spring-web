package com.example.guitarzone;

import com.example.guitarzone.config.I18NConfig;
import com.example.guitarzone.config.WebConfig;
import com.example.guitarzone.controllers.ProductController;
import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.Role;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.model.enums.UserRole;
import com.example.guitarzone.service.ProductService;
import com.example.guitarzone.service.WishlistService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@Import({I18NConfig.class, WebConfig.class})
@ActiveProfiles("test")
@EnableWebMvc
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private ProductService productService;

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    public void testViewGuitar() throws Exception {
        mockMvc.perform(get("/guitars"))
                .andExpect(status().isOk())
                .andExpect(view().name("guitars"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testViewGuitarDetails() throws Exception {
        ProductDetailsDTO productDetails = new ProductDetailsDTO();
        productDetails.setId(1L);
        productDetails.setName("Test Guitar");
        productDetails.setType("Acoustic");
        productDetails.setPrice(100.00);
        productDetails.setStockStatus("in stock");
        productDetails.setRating(4.5);
        productDetails.setNumberOfReviews(10);
        productDetails.setDescription("A great acoustic guitar.");
        productDetails.setImageUrl("test-image-url");

        when(productService.getProductDetails(anyLong())).thenReturn(productDetails);

        mockMvc.perform(get("/guitars/guitar-details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("guitar-details"))
                .andExpect(model().attributeExists("productDetails"))
                .andExpect(model().attribute("productDetails", productDetails));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddToCart() throws Exception {
        // You may need to mock service methods here if any service method is called in the /cart/add endpoint.
        // For example: when(cartService.addToCart(anyLong(), anyInt(), anyString())).thenReturn(...);

        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddToWishlist() throws Exception {
        mockMvc.perform(post("/users/wishlist/add")
                        .param("productId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/wishlist"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testRemoveFromWishlist() throws Exception {
        mockMvc.perform(post("/users/wishlist/remove")
                        .param("productId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/wishlist"));
    }
}
