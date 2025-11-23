package exception;

/**
 * Exception được ném ra khi không tìm thấy sản phẩm theo ID hoặc tên
 */
public class ProductNotFoundException extends Exception {
    private String searchKey;
    private String searchType; // "ID" hoặc "NAME"

    public ProductNotFoundException(String searchKey, String searchType) {
        super(String.format("Không tìm thấy sản phẩm với %s: %s", searchType, searchKey));
        this.searchKey = searchKey;
        this.searchType = searchType;
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    // Getters
    public String getSearchKey() { return searchKey; }
    public String getSearchType() { return searchType; }
}
