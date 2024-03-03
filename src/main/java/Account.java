import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Роман on 25.02.2024
 **/
public class Account {

    private String name;
    private final Map<Valuta, Long> accountBalance;
    private final Deque<UndoService> history;

    public Account(String name) {
        checkName(name);
        this.name = name;
        this.accountBalance = new HashMap<>();
        this.history = new ArrayDeque<>();
    }

    public void setName(String name) {
        checkName(name);
        String tmp = this.name;
        history.push(() -> Account.this.name = tmp);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<Valuta, Long> getAccountBalance() {
        return new HashMap<Valuta, Long>(accountBalance);
    }

    public void updateAccountBalance(String currency, Long balance) {
        try {
            if (balance < 0) throw new IllegalArgumentException("Колличество валюты не может быть отрицательным.");
            Long tmp = accountBalance.get(Valuta.valueOf(currency));
            if (tmp != null) {
                history.push(() -> Account.this.accountBalance.put(Valuta.valueOf(currency), tmp));
            } else {
                history.push(() -> Account.this.accountBalance.remove(Valuta.valueOf(currency)));
            }
            accountBalance.put(Valuta.valueOf(currency), balance);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (NullPointerException e) {
            throw new NullPointerException("Валюта или Баланс не можут быть null.");
        }
    }

    public void undo() {
        try {
            history.pop().make();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Изменений банковского счета открытого на имя %s - нет.", this.name));
        }
    }

    public AccountHistory save() {
        return new AccountHistory(this.name, this.accountBalance);
    }

    private void checkName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Имя владелца аккоунта не может пустым.");
        }
    }

    public class AccountHistory {
        @Getter
        private final String dateTime;
        @Getter
        private final String name;

        private final Map<Valuta, Long> accountBalance;

        public AccountHistory(String name, Map<Valuta, Long> accountBalance) {
            this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.name = name;
            this.accountBalance = new HashMap<>(accountBalance);
        }

        public Map<Valuta, Long> getAccountBalance() {
            return new HashMap<>(accountBalance);
        }

        public Account load() {
            Account account = new Account(name);
            for (Valuta k : accountBalance.keySet()) {
                account.updateAccountBalance(k.name(), accountBalance.get(k));
            }
            return account;
        }

    }

    public enum Valuta {
        RUB,
        USD,
        EUR,
        TRY,
        JPY;
    }
}
