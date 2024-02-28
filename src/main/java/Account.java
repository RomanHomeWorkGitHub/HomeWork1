import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by Роман on 25.02.2024
 **/
public class Account {

    private String name;
    private final Map<String, Long> accountBalance;
    private final Deque<Object> history;

    public Account(String name) {
        checkName(name);
        this.name = name;
        this.accountBalance = new HashMap<>();
        this.history = new LinkedList<>();
    }

    private Account(String name, Map<String, Long> accountBalance) {
        this.name = name;
        Map<String, Long> accBal = new HashMap<>();
        for(String key : accountBalance.keySet()) {
            accBal.put(key, accountBalance.get(key));
        }
        this.accountBalance = accBal;
        this.history = null;
    }

    public void setName(String name) {
        checkName(name);
        history.addLast(this.name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, Long> getAccountBalance() {
        Map<String, Long> accountBalance = new HashMap<>();
        for(String key : this.accountBalance.keySet()) {
            accountBalance.put(key, this.accountBalance.get(key));
        }
        return accountBalance;
    }

    public void updateAccountBalance(String currency, Long balance) {
        try {
            if (balance < 0) throw new IllegalArgumentException("Колличество валюты не может быть отрицательным.");
            Valuta.valueOf(currency);
            history.addLast(this.getAccountBalance());
            accountBalance.put(currency, balance);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (NullPointerException e) {
            throw new NullPointerException("Валюта или Баланс не можут быть null.");
        }
    }

    public void undo() {
        try {
            Object object = history.getLast();
            if (object instanceof String) {
                this.name = (String) object;
            } else if (object instanceof Map) {
                Map<String, Long> map = (Map<String, Long>) history.getLast();
                this.accountBalance.clear();
                this.accountBalance.putAll(map);
            }
            history.removeLast();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Изменений банковского счета открытого на имя %s - нет.", this.name));
        }
    }

    public Account save() {
        Account account = new Account(this.name, this.accountBalance);
        history.addLast(account);
        return account;
    }

    private void checkName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Имя владелца аккоунта не может пустым.");
        }
    }

    public enum Valuta {
        RUB,
        USD,
        EUR,
        TRY,
        JPY
    }
}
