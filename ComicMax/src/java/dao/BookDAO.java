/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Book;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.PageData;
/**
 *
 * @author emsin
 */
//ORDER{"new","old","cheap","expensive"}

public class BookDAO extends DAO{
    private String setValue(String param, String defaultValue){
       defaultValue = defaultValue == null || defaultValue.equals("null")? "null" : defaultValue;
       return param == null || param.equals("null")? defaultValue : param;
   }
    private Book newBook(ResultSet rs){
        Book book = null;
        try {
            book = new Book(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getFloat(7),
                        rs.getInt(8),
                        rs.getInt(9),
                        rs.getFloat(10),
                        rs.getInt(11)
                                    );
        } catch (Exception e) {}
        return book;
    }
    private Book getBookByQuery(String query){
        ResultSet rs = this.getResult(query);
        try {
            while(rs.next()){
                return this.newBook(rs);
            }
        } catch (Exception e) {}
        return null;
    }
    private List<Book> getListByQuery(String query){
        List<Book> list = new ArrayList<>();
        ResultSet rs = this.getResult(query);
        try {
            while(rs.next()){
                list.add(this.newBook(rs));
            }
        } catch (Exception e) {}
        return list;
    }
    public int getCount(String _keywords, String[] _category_ids, String _author_id, String _publisher_id){
        int count = 0;
        String keywords = this.setValue(_keywords, "");
        String category_ids = _category_ids != null? "'"+String.join(",", _category_ids)+"'": "null";
        String author_id = this.setValue(_author_id, null);
        String publisher_id = this.setValue(_publisher_id, null);
        String query = "exec countSearchBooks \n"+
                "@keywords = '"+keywords+"',\n" +
                "@category_id = "+category_ids+",\n" +
                "@publisher_id = "+publisher_id+",\n" +
                "@author_id = "+author_id;
        ResultSet rs = this.getResult(query);
        try {
            while(rs.next()){
                return rs.getInt(1);
            }
        } catch (Exception e) {}
        return count;
    }
    public List<Book> searchBooks(String _keywords, String[] _category_ids, String _author_id, String _publisher_id, String _page_index,String _amount_per_page,String _id_order, String _price_order){
        String keywords = this.setValue(_keywords, "");
        String category_ids = _category_ids != null? "'"+String.join(",", _category_ids)+"'": "null";
        String author_id = this.setValue(_author_id, null);
        String publisher_id = this.setValue(_publisher_id, null);
        String page_index = this.setValue(_page_index, "1");
        String amount_per_page = this.setValue(_amount_per_page, ""+PageData.amount_per_page);
        String id_order = this.setValue(_id_order, "DESC");
        String price_order = this.setValue(_price_order, "ASC");
        String query ="EXEC searchBooks \n" +
                "@keywords = '"+keywords+"',\n" +
                "@category_id = "+category_ids+",\n" +
                "@publisher_id = "+publisher_id+",\n" +
                "@author_id = "+author_id+",\n" +
                "@page_index = "+page_index+",\n" +
                "@amount_per_page = "+amount_per_page+",\n" +
                "@idOrder = '"+id_order+"',\n" +
                "@priceOrder = '"+price_order+"'";
        return this.getListByQuery(query);
    }
    public List<Book> getAllBooks(){
        String query = "Select * from Book";
        return this.getListByQuery(query);
    }
    public List<Book> getTop(int n){
        String query = "select top "+ n +" * from Book order by sold DESC"; 
        return this.getListByQuery(query);
    }
    public Book getBookById(int id){
        String query = "select * from Book where id = "+id; 
        return this.getBookByQuery(query);
    }
    public List<Book> getAllBooksLike(String s){
        String query = "exec getAllBooksLike "+Utils.searchPrepocessor(s); 
        return this.getListByQuery(query);
    }
    public List<Book> getTopNewestBooks(int n){
        String query = "select top " + n + " * from Book order by id DESC"; 
        return this.getListByQuery(query);
    }
    public List<Book> getBooksByCategoryId(int x, int n){
        String query = "select top " + n + " * from Book where publisher_id = " + x; 
        return this.getListByQuery(query);
    }
    public List<Book> getBooksByAuthorId(int x, int n){
        String query = "select top " + n + " * from Book where author_id= " + x; 
        return this.getListByQuery(query);
    }
    public List<Book> getBooksByLanguage(int x, int n){
        String query = "select top " + n + " * from Book where language = " + x; 
        return this.getListByQuery(query);
    }
    public List<Book> getBooksByPrice(int x, int n){
        String query = "select top" + n + "* from Book where price <= " + x; 
        return this.getListByQuery(query);
    }
    public List<Book> getBooksByPageIndex(int index, int amount, String orderBy, String order){
        if(orderBy==null) orderBy = "id";
        if(order==null) order = "ASC";
        String query =  "SELECT *\n" +
                        "FROM Book\n" +
                        "ORDER BY " +orderBy+ " " +order+ "\n" +
                        "OFFSET (" +index+" - 1) * "+ amount +" ROWS\n" +
                        "FETCH NEXT " +amount+" ROWS ONLY;"; 
        return this.getListByQuery(query);
    }
    public List<Book> filterBooksByCategories(List<Book> list, String[] category_ids_array){
        List<String> category_ids = new ArrayList<>();
        if(category_ids_array != null){
            category_ids.addAll(Arrays.asList(category_ids_array));
            for(Book book: list){
                if(!book.getCategoryIds().containsAll(category_ids)) list.remove(book);
            }
        }
        return list;
    }
    public List<Book> pagingByBookList(List<Book> list, int index, int amount){
        List<Book> ans = new ArrayList<>();
        int begin = (index-1)*amount,
            end = begin + amount;
        for(int i= begin; i<end;i++){
            ans.add(list.get(i));
        }
        return ans;
    }
    public int getCountPage(int n, int productPerPage){
        return (int) Math.ceil((float)n/(float)productPerPage);
    }
    public List<Book> getRelateBook(Book book, int n){
        String query = "";
        return this.getListByQuery(query);
    }
    public Book modifyProduct(Book book) throws Exception{
        BookDAO bookdao= new BookDAO();
        String query;
        int effectRow=0;
        try {
            DBContext db = DBContext.getInstance();
            Connection conn = db.getConnection();
            PreparedStatement ps;
            query = "UPDATE book SET name = ?, img = ?, amount = ?, price = ? WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, book.getName().trim());
            ps.setString(2, book.getImg().trim());
            ps.setInt(3, book.getAmount());
            ps.setFloat(4, book.getPrice());
            ps.setInt(5, book.getId());
            effectRow = ps.executeUpdate();
        }
         catch (Exception e) {}
        return effectRow>0? bookdao.getBookById(book.getId()) : null;
    }
    public Book deleteProduct(int id) throws Exception{
        BookDAO bookdao= new BookDAO();
        String query;
        int effectRow=0;
        try {
            DBContext db = DBContext.getInstance();
            Connection conn = db.getConnection();
            PreparedStatement ps;
            query = "DELETE FROM BOOK WHERE id = "+id;
            ps = conn.prepareStatement(query);
            effectRow = ps.executeUpdate();
        }
         catch (Exception e) {}
        return effectRow>0? bookdao.getBookById(id) : null;
    }
}

