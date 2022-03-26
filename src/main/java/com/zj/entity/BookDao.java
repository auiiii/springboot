package com.zj.entity;

import java.util.List;

public interface BookDao {
    public Book selectById(int id);

    public List<Book> selectAll();
}
