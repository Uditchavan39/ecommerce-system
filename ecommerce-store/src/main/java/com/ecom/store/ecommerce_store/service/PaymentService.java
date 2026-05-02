package com.ecom.store.ecommerce_store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecom.store.ecommerce_store.dto.PaymentRequest;
import com.ecom.store.ecommerce_store.exception.InsufficientStockException;
import com.ecom.store.ecommerce_store.exception.OrderNotFoundException;
import com.ecom.store.ecommerce_store.exception.PaymentFailedException;
import com.ecom.store.ecommerce_store.model.Cart;
import com.ecom.store.ecommerce_store.model.CartItem;
import com.ecom.store.ecommerce_store.model.Order;
import com.ecom.store.ecommerce_store.model.OrderItem;
import com.ecom.store.ecommerce_store.model.OrderStatus;
import com.ecom.store.ecommerce_store.model.Payment;
import com.ecom.store.ecommerce_store.model.PaymentStatus;
import com.ecom.store.ecommerce_store.model.Product;
import com.ecom.store.ecommerce_store.model.User;
import com.ecom.store.ecommerce_store.repository.CartRepository;
import com.ecom.store.ecommerce_store.repository.OrderRepository;
import com.ecom.store.ecommerce_store.repository.PaymentRepository;
import com.ecom.store.ecommerce_store.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductRepository productRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
            InventoryService inventoryService, CartRepository cartRepository, CartService cartService,
            UserService userService, ProductRepository productRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.productRepository = productRepository;
    }

    /**
     * Place order with transactional integrity
     * Steps:
     * 1. Validate cart and inventory
     * 2. Reserve inventory
     * 3. Create order with PAYMENT_PENDING status
     * 4. Create payment with PENDING status
     * 5. Clear cart
     */
    @Transactional
    public Order placeOrder(Long userId) {
        // Validate cart
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<CartItem> items = cart.getItems();
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate and reserve inventory
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : items) {
            int requestedQty = cartItem.getQuantity();
            Long productId = cartItem.getProduct().getId();

            // Check stock availability
            int availableQty = inventoryService.getAvailableQuantity(productId);
            if (availableQty < requestedQty) {
                throw new InsufficientStockException(productId, requestedQty, availableQty);
            }

            // Reserve inventory
            inventoryService.reserve(productId, requestedQty);

            // Create OrderItem
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setPurchasePrice(product.getPrice());
            orderItem.setQuantity(requestedQty);
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                    BigDecimal.valueOf(product.getPrice() != null ? product.getPrice() : 0)
                            .multiply(BigDecimal.valueOf(requestedQty)));
        }

        // Create order with PAYMENT_PENDING status
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(orderItems);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        order = orderRepository.save(order);

        // Create payment record with PENDING status
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    /**
     * Process payment with simulator
     * Payment methods supported: CARD, UPI, COD
     * Uses fake transaction ID generation
     */
    @Transactional
    public Payment processPayment(Long orderId, PaymentRequest request) {
        // Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Validate order is in PAYMENT_PENDING state
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Order is not in PAYMENT_PENDING state");
        }

        // Validate payment method
        String paymentMethod = request.getPaymentMethod();
        if (!paymentMethod.equalsIgnoreCase("CARD") && !paymentMethod.equalsIgnoreCase("UPI")
                && !paymentMethod.equalsIgnoreCase("COD")) {
            throw new RuntimeException("Invalid payment method: " + paymentMethod);
        }

        // Validate amount matches order total
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            throw new RuntimeException("Payment amount does not match order total");
        }

        // Get or create payment record
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        Payment payment = existingPayment.orElse(new Payment());

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(paymentMethod);

        if (paymentSuccess) {
            // Payment SUCCESS
            String transactionId = generateTransactionId();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setTransactionId(transactionId);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            // Update order status to PAID and PROCESSING
            order.setStatus(OrderStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // Confirm inventory reservation (finalize the reservation)
            for (OrderItem item : order.getItems()) {
                inventoryService.confirm(item.getProduct().getId(), item.getQuantity());
            }

            return payment;

        } else {
            // Payment FAILED - ROLLBACK
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            // CRITICAL: Restore inventory on payment failure
            for (OrderItem item : order.getItems()) {
                inventoryService.release(item.getProduct().getId(), item.getQuantity());
            }

            // Update order status to CANCELLED
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            throw new PaymentFailedException(orderId, "Payment processing failed. Inventory has been restored.");
        }
    }

    /**
     * Simulate payment processing
     * Simple logic: COD always succeeds, CARD/UPI success 80% of the time (fake)
     */
    private boolean simulatePaymentProcessing(String paymentMethod) {
        if (paymentMethod.equalsIgnoreCase("COD")) {
            return true; // Cash on delivery always succeeds
        }

        // For CARD and UPI, simulate 80% success rate
        return Math.random() < 0.8;
    }

    /**
     * Generate fake transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    /**
     * Get payment by transaction ID
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

    /**
     * Get payment by order ID
     */
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
}
