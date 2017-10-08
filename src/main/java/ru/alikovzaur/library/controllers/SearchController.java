package ru.alikovzaur.library.controllers;

import ru.alikovzaur.library.entityes.BookEntity;
import ru.alikovzaur.library.enums.SearchTypeEnum;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

@Named
@SessionScoped
public class SearchController implements Serializable {
    private Long selectedGenre;
    private String searchType;
    private String searchField;
    private List<BookEntity> books = new ArrayList<>();
    private int bookOnPage = 2;
    private ArrayList<Integer> pageCount = new ArrayList<>();
    private String typeSearch = "all";
    private Long genreId = -1L;
    private int bookCount = 0;
    private int selectedPage = 1;
    private Query query = null;

    @PersistenceContext(unitName = "libraryPU")
    private EntityManager entityManager;

    @PostConstruct
    public void postConstruct() {
        setSelectedGenre(-1L);
        this.fillBooks();
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchField() {
        return searchField;
    }

    public Long getSelectedGenre() {
        return selectedGenre;
    }

    public void setSelectedGenre(Long selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public List<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(List<BookEntity> books) {
        this.books = books;
    }

    public ArrayList<Integer> getPageCount() {
        return pageCount;
    }

    public void setPageCount(ArrayList<Integer> pageCount) {
        this.pageCount = pageCount;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public int getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int selectedPage) {
        this.selectedPage = selectedPage;
    }

    public int getBookOnPage() {
        return bookOnPage;
    }

    public void setBookOnPage(int bookOnPage) {
        this.bookOnPage = bookOnPage;
    }

    public byte[] getImage(long id){
        BookEntity book = entityManager.find(BookEntity.class, id);
        return book.getImage();
    }

    public byte[] getPdf(long id){
        BookEntity book = entityManager.find(BookEntity.class, id);
        return book.getContent();
    }

    @SuppressWarnings("unchecked")
    public void fillBooks(){
        selectedPage = 1;
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        if (params.size() > 0){
            if(params.get("type_search") != null){
                typeSearch = params.get("type_search");
            }
            if (params.get("selected_page") != null){
                selectedPage = Integer.valueOf(params.get("selected_page"));
            }
        }

        switch (typeSearch) {
            case "all": {
                query = entityManager.createQuery("select book from BookEntity book order by book.name");
                setSearchField("");
                setSelectedGenre(0L);
                break;
            }
            case "genre": {
                if (params.get("genre_id") != null){
                    genreId = Long.valueOf(params.get("genre_id"));
                }
                query = entityManager.createQuery("select book from BookEntity book where genre.id = :id order by book.name");
                query.setParameter("id", genreId);
                setSelectedGenre(genreId);
                setSearchField("");
                break;
            }
            case "search": {
                if (searchType.equals(SearchTypeEnum.Название.toString())) {
                    query = entityManager.createQuery("select book from BookEntity book where book.name like concat('%', :bookName, '%') order by book.name");
                    query.setParameter("bookName", searchField);
                } else if (searchType.equals(SearchTypeEnum.Автор.toString())) {
                    query = entityManager.createQuery("select book from BookEntity book where book.author.fio like concat('%', :author, '%') order by book.name");
                    query.setParameter("author", searchField);
                }
                setSelectedGenre(-1L);
                break;
            }
        }

        if (query != null && query.getResultList() != null) {
            bookCount = query.getResultList().size();
            int first = selectedPage * bookOnPage - bookOnPage;
            int max = bookOnPage;
            query.setMaxResults(max);
            query.setFirstResult(first);
            books.clear();
            books = query.getResultList();
        }

//        int pages = bookCount > 0 ? (int) Math.ceil((double)bookCount / bookOnPage) : 0;
        int pages;
        if (bookCount > 0 && bookOnPage > 0){
            pages = (int) Math.ceil((double)bookCount / bookOnPage);
        } else {
            pages = 0;
        }

        pageCount.clear();
        for (int i = 1; i <= pages; i++){
            pageCount.add(i);
        }
    }
}
