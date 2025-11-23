package exception;

/**
 * Exception được ném ra khi xuất kho nhưng không đủ hàng tồn kho
 */
public class OutOfStockException extends Exception {
    private String productId;
    private String productName;
    private int requestedQuantity;
    private int availableQuantity;

    public OutOfStockException(String productId, String productName,
                               int requestedQuantity, int availableQuantity) {
        super(String.format("Không đủ hàng! Sản phẩm '%s' (ID: %s) - Yêu cầu: %d, Tồn kho: %d",
                productName, productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public OutOfStockException(String message) {
        super(message);
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getRequestedQuantity() { return requestedQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
}
