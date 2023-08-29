package com.smallacademy.userroles;

public class ShoppingListModel
{
    private String topic;
    private String description;
    private int id;

    public ShoppingListModel(String topic, String description)
    {
        this.topic = topic;
        this.description = description;
    }

    public ShoppingListModel(String topic, String description, int id)
    {
        this.topic = topic;
        this.description = description;
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public String getTopic()
    {
        return topic;
    }

    public String getDescription()
    {
        return description;
    }
}
