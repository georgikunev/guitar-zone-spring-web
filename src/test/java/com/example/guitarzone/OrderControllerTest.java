package com.example.guitarzone;

import com.example.guitarzone.config.I18NConfig;
import com.example.guitarzone.config.WebConfig;
import com.example.guitarzone.controllers.OrderController;
import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.OrderService;
import com.example.guitarzone.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@Import({I18NConfig.class, WebConfig.class})
@ActiveProfiles("test")
@EnableWebMvc
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();

        // Initialize a User object to be used in the tests
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");

        // Ensure UserService mock returns the user
        when(userService.findByEmail(anyString())).thenReturn(user);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testPlaceOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAddress("123 Main St");
        orderDTO.setCity("City");
        orderDTO.setCountry("Country");
        orderDTO.setZip("12345");

        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotal(100.00);

        when(cartService.getCartByUserId(user.getId())).thenReturn(cartDTO);

        mockMvc.perform(post("/checkout")
                        .flashAttr("orderDTO", orderDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirmation"))
                .andExpect(model().attributeExists("orderConfirmation"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testShowCheckoutPage() throws Exception {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotal(100.00);

        when(cartService.getCartByUserId(user.getId())).thenReturn(cartDTO);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attributeExists("orderDTO"))
                .andExpect(model().attribute("isCartEmpty", false));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testGetOrderHistory() throws Exception {
        mockMvc.perform(get("/users/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-history"));
    }
}
