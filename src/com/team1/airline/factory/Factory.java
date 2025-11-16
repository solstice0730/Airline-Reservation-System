package com.team1.airline.factory;
import java.util.Scanner;

public interface Factory<T> extends Manageable {
        T create(Scanner scan);
    }
}
