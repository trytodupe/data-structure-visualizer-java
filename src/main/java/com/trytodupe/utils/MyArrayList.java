package com.trytodupe.utils;

import com.trytodupe.Main;
import com.trytodupe.datastructure.tree.HuffmanNode;

import java.util.ArrayList;

public class MyArrayList<E> extends ArrayList<E> {

    @Override
    public boolean add(E newElement) {
        boolean result = super.add(newElement);
        if (Main.DEBUG) {
            System.out.println("addding: ");
            for (E e : this) {
                if (e instanceof HuffmanNode) {
                    System.out.println(e);

                } else {
                    System.out.println(this);
                }
            }
            System.out.println();
        }
        return result;
    }

    @Override
    public boolean remove(Object e) {
        boolean result = super.remove(e);
        if (Main.DEBUG) {

            if (e instanceof HuffmanNode) {
                System.out.println("removing: " + e);
            } else {
                System.out.println("removing: ");
            }

            for (Object o : this) {
                if (o instanceof HuffmanNode) {
                    System.out.println(o);
                } else  {
                    System.out.println(this);
                }
            }
            System.out.println();
        }
        return result;
    }
}

