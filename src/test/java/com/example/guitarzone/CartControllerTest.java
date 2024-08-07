package com.example.guitarzone;

import com.example.guitarzone.config.I18NConfig;
import com.example.guitarzone.config.WebConfig;
import com.example.guitarzone.controllers.CartController;
import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.entities.Role;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.model.enums.UserRole;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@Import({I18NConfig.class, WebConfig.class})
@ActiveProfiles("test")
@EnableWebMvc
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, WithSecurityContextTestExecutionListener.class })
public class CartControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private User user;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testGetCart() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");

        Role role = new Role(1L, UserRole.USER);
        user.setRoles(Collections.singleton(role));

        CustomUserDetails userDetails = new CustomUserDetails(user, Collections.emptyList());

        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Collections.emptyList());
        cartDTO.setTotal(0.0);

        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(cartService.getCartByUserId(1L)).thenReturn(cartDTO);

        mockMvc.perform(get("/cart")
                        .principal(() -> "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"));

        verify(userService, times(1)).findByEmail("user@example.com");
        verify(cartService, times(1)).getCartByUserId(1L);
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddItemToCart() throws Exception {
        when(userService.findByEmail("user")).thenReturn(user);
        when(cartService.addItemToCart(1L, 1L, 1)).thenReturn("Product added to cart");

        mockMvc.perform(post("/cart/add")
                        .principal(() -> "user")
                        .param("productId", "1")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guitars"));

        verify(userService, times(1)).findByEmail("user");
        verify(cartService, times(1)).addItemToCart(1L, 1L, 1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testRemoveItemFromCart() throws Exception {
        when(userService.findByEmail("user")).thenReturn(user);

        mockMvc.perform(post("/cart/remove")
                        .principal(() -> "user")
                        .param("itemId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(userService, times(1)).findByEmail("user");
        verify(cartService, times(1)).removeItemFromCart(1L, 1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testIncrementCartItem() throws Exception {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Collections.emptyList());
        cartDTO.setTotal(0.0);

        when(userService.findByEmail("user")).thenReturn(user);
        when(cartService.getCartByUserId(1L)).thenReturn(cartDTO);

        mockMvc.perform(post("/cart/increment")
                        .principal(() -> "user")
                        .param("itemId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"));

        verify(userService, times(1)).findByEmail("user");
        verify(cartService, times(1)).incrementCartItem(1L, 1L);
        verify(cartService, times(1)).getCartByUserId(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDecrementCartItem() throws Exception {
        when(userService.findByEmail("user")).thenReturn(user);

        mockMvc.perform(post("/cart/decrement")
                        .principal(() -> "user")
                        .param("itemId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(userService, times(1)).findByEmail("user");
        verify(cartService, times(1)).decrementCartItem(1L, 1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testProceedToCheckout() throws Exception {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Collections.emptyList());
        cartDTO.setTotal(0.0);

        when(userService.findByEmail("user")).thenReturn(user);
        when(cartService.getCartByUserId(1L)).thenReturn(cartDTO);

        mockMvc.perform(post("/cart/proceed-to-checkout")
                        .with(csrf())  // Include CSRF token
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attributeExists("isCartEmpty"))
                .andExpect(model().attributeExists("orderDTO"));

        verify(userService, times(1)).findByEmail("user@example.com");
        verify(cartService, times(1)).getCartByUserId(1L);
    }
}
