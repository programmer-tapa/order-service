package com.example.orderservice.app.core.orders.features.createOrder.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.entities.OrderStatus;
import com.example.orderservice.app.core.orders.features.createOrder.exceptions.InvalidOrderException;
import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.InputOrderItem;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;

@ExtendWith(MockitoExtension.class)
@DisplayName("USECASE_CreateOrder Tests")
class USECASE_CreateOrderTest {

    @Mock
    private INTERFACE_HELPER_CreateOrder mockHelper;

    private USECASE_CreateOrder usecase;

    @BeforeEach
    void setUp() {
        usecase = new USECASE_CreateOrder(mockHelper);
    }

    // ==================== Helper Methods ====================

    private InputOrderItem createValidItem() {
        return new InputOrderItem("PROD-001", 2, new BigDecimal("25.00"));
    }

    private InputOrderItem createValidItem(String productId, int quantity, BigDecimal price) {
        return new InputOrderItem(productId, quantity, price);
    }

    private INPUT_CreateOrder createValidInput() {
        return new INPUT_CreateOrder(
                "CUST-123",
                List.of(createValidItem()),
                "USD");
    }

    private Order createBuiltOrder() {
        Order order = new Order();
        order.setCustomerId("CUST-123");
        order.setCurrency("USD");
        return order;
    }

    private Order createSavedOrder() {
        Order order = new Order();
        order.setId("ORDER-001");
        order.setCustomerId("CUST-123");
        order.setCurrency("USD");
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(new BigDecimal("50.00"));
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    // ==================== Success Cases ====================

    @Nested
    @DisplayName("Success Cases")
    class SuccessCases {

        @Test
        @DisplayName("Should successfully create order with valid input")
        void execute_WithValidInput_ReturnsSuccessfulOutput() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);
            doNothing().when(mockHelper).publishEvent(savedOrder);

            // Act
            OUTPUT_CreateOrder output = usecase.execute(input);

            // Assert
            assertNotNull(output);
            assertEquals("ORDER-001", output.orderId());
            assertEquals("CREATED", output.status());
            assertEquals(new BigDecimal("50.00"), output.totalAmount());
            assertEquals("USD", output.currency());
            assertNotNull(output.createdAt());
        }

        @Test
        @DisplayName("Should call validateInput on helper")
        void execute_WithValidInput_CallsValidateInput() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);

            // Act
            usecase.execute(input);

            // Assert
            verify(mockHelper, times(1)).validateInput(input);
        }

        @Test
        @DisplayName("Should call buildOrder on helper")
        void execute_WithValidInput_CallsBuildOrder() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);

            // Act
            usecase.execute(input);

            // Assert
            verify(mockHelper, times(1)).buildOrder(input);
        }

        @Test
        @DisplayName("Should call saveOrder on helper")
        void execute_WithValidInput_CallsSaveOrder() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);

            // Act
            usecase.execute(input);

            // Assert
            verify(mockHelper, times(1)).saveOrder(builtOrder);
        }

        @Test
        @DisplayName("Should call publishEvent after saving order")
        void execute_WithValidInput_CallsPublishEvent() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);

            // Act
            usecase.execute(input);

            // Assert
            verify(mockHelper, times(1)).publishEvent(savedOrder);
        }

        @Test
        @DisplayName("Should call helper methods in correct order")
        void execute_WithValidInput_CallsMethodsInOrder() {
            // Arrange
            INPUT_CreateOrder input = createValidInput();
            Order builtOrder = createBuiltOrder();
            Order savedOrder = createSavedOrder();
            doNothing().when(mockHelper).validateInput(input);
            when(mockHelper.buildOrder(input)).thenReturn(builtOrder);
            when(mockHelper.saveOrder(builtOrder)).thenReturn(savedOrder);
            doNothing().when(mockHelper).publishEvent(savedOrder);

            // Act
            usecase.execute(input);

            // Assert - Verify order of calls
            var inOrder = inOrder(mockHelper);
            inOrder.verify(mockHelper).validateInput(input);
            inOrder.verify(mockHelper).buildOrder(input);
            inOrder.verify(mockHelper).saveOrder(builtOrder);
            inOrder.verify(mockHelper).publishEvent(savedOrder);
        }
    }

    // ==================== Validation Failure Cases ====================

    @Nested
    @DisplayName("Customer ID Validation")
    class CustomerIdValidation {

        @Test
        @DisplayName("Should throw exception when customer ID is null")
        void execute_WithNullCustomerId_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder(null, List.of(createValidItem()), "USD");
            doThrow(new InvalidOrderException("Customer ID is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Customer ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when customer ID is empty")
        void execute_WithEmptyCustomerId_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("", List.of(createValidItem()), "USD");
            doThrow(new InvalidOrderException("Customer ID is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Customer ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when customer ID is blank")
        void execute_WithBlankCustomerId_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("   ", List.of(createValidItem()), "USD");
            doThrow(new InvalidOrderException("Customer ID is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Customer ID is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Items Validation")
    class ItemsValidation {

        @Test
        @DisplayName("Should throw exception when items is null")
        void execute_WithNullItems_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", null, "USD");
            doThrow(new InvalidOrderException("Order must contain at least one item"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Order must contain at least one item", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when items list is empty")
        void execute_WithEmptyItems_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", Collections.emptyList(), "USD");
            doThrow(new InvalidOrderException("Order must contain at least one item"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Order must contain at least one item", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Currency Validation")
    class CurrencyValidation {

        @Test
        @DisplayName("Should throw exception when currency is null")
        void execute_WithNullCurrency_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(createValidItem()), null);
            doThrow(new InvalidOrderException("Currency is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Currency is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when currency is empty")
        void execute_WithEmptyCurrency_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(createValidItem()), "");
            doThrow(new InvalidOrderException("Currency is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Currency is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when currency is blank")
        void execute_WithBlankCurrency_ThrowsInvalidOrderException() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(createValidItem()), "   ");
            doThrow(new InvalidOrderException("Currency is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Currency is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Order Item Validation")
    class OrderItemValidation {

        @Test
        @DisplayName("Should throw exception when product ID is null")
        void execute_WithNullProductId_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem(null, 1, new BigDecimal("10.00"));
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Product ID is required for all items"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Product ID is required for all items", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when product ID is blank")
        void execute_WithBlankProductId_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("   ", 1, new BigDecimal("10.00"));
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Product ID is required for all items"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Product ID is required for all items", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when quantity is zero")
        void execute_WithZeroQuantity_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("PROD-001", 0, new BigDecimal("10.00"));
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Quantity must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Quantity must be greater than zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void execute_WithNegativeQuantity_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("PROD-001", -5, new BigDecimal("10.00"));
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Quantity must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Quantity must be greater than zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when unit price is null")
        void execute_WithNullUnitPrice_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("PROD-001", 1, null);
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Unit price must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Unit price must be greater than zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when unit price is zero")
        void execute_WithZeroUnitPrice_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("PROD-001", 1, BigDecimal.ZERO);
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Unit price must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Unit price must be greater than zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when unit price is negative")
        void execute_WithNegativeUnitPrice_ThrowsInvalidOrderException() {
            // Arrange
            InputOrderItem invalidItem = new InputOrderItem("PROD-001", 1, new BigDecimal("-10.00"));
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", List.of(invalidItem), "USD");
            doThrow(new InvalidOrderException("Unit price must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Unit price must be greater than zero", exception.getMessage());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should not call buildOrder, saveOrder or publishEvent when validation fails")
        void execute_WithInvalidInput_DoesNotCallOtherHelperMethods() {
            // Arrange
            INPUT_CreateOrder input = new INPUT_CreateOrder(null, List.of(createValidItem()), "USD");
            doThrow(new InvalidOrderException("Customer ID is required"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            assertThrows(InvalidOrderException.class, () -> usecase.execute(input));
            verify(mockHelper, times(1)).validateInput(input);
            verify(mockHelper, never()).buildOrder(any());
            verify(mockHelper, never()).saveOrder(any());
            verify(mockHelper, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should validate first invalid item in list")
        void execute_WithSecondItemInvalid_ThrowsException() {
            // Arrange
            List<InputOrderItem> items = List.of(
                    createValidItem("PROD-001", 1, new BigDecimal("10.00")),
                    new InputOrderItem("PROD-002", 0, new BigDecimal("20.00")) // Invalid quantity
            );
            INPUT_CreateOrder input = new INPUT_CreateOrder("CUST-123", items, "USD");
            doThrow(new InvalidOrderException("Quantity must be greater than zero"))
                    .when(mockHelper).validateInput(input);

            // Act & Assert
            InvalidOrderException exception = assertThrows(
                    InvalidOrderException.class,
                    () -> usecase.execute(input));
            assertEquals("Quantity must be greater than zero", exception.getMessage());
        }
    }
}
