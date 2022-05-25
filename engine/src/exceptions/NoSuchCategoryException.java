package exceptions;

import java.util.ArrayList;
import java.util.List;

public class NoSuchCategoryException extends Exception{
    private String invalidCategory;
    private List<String> categories;

    public NoSuchCategoryException(String invalidCategory, List<String> categories){
        this.categories = new ArrayList<>();
        for (String category: categories)
            this.categories.add(category.trim());
        this.invalidCategory = invalidCategory;
    }
    public String getInvalidCategory(){
        return invalidCategory;
    }
    public List<String> getCategories(){
        return categories;
    }
}
