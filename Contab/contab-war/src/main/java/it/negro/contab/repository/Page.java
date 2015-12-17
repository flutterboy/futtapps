package it.negro.contab.repository;

public class Page {
    private int pageNumber = 0;
    private int elementsNumber = 10;

    public Page(){}

    public Page(int pageNumber){
        this.pageNumber = pageNumber;
    }

    public Page(int pageNumber, int elementsNumber){
        this.pageNumber = pageNumber;
        this.elementsNumber = elementsNumber;
    }

    public int skip(){
        return (pageNumber -1) * elementsNumber;
    }

    public int limit(){
        return elementsNumber;
    }
}
