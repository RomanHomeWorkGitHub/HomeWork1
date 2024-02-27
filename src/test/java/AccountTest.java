import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    Account account = new Account("Валера");

    @Test
    void setName_isEmptyName() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> account.setName(" "));
        assertEquals("Имя владелца аккоунта не может пустым.", thrown.getMessage());
    }

    @Test
    void setName_isBlankName() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> account.setName(""));
        assertEquals("Имя владелца аккоунта не может пустым.", thrown.getMessage());
    }

    @Test
    void setName_isNullName() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> account.setName(null));
        assertEquals("Имя владелца аккоунта не может пустым.", thrown.getMessage());
    }

    @Test
    void setName_ok() {
        account.setName("Иван");
        assertEquals("Иван", account.getName());
    }

    @Test
    void createAccount_isEmtyName() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> new Account(""));
        assertEquals("Имя владелца аккоунта не может пустым.", thrown.getMessage());
    }

    @Test
    void getName_ok() {
        assertEquals("Валера", account.getName());
    }

    @Test
    void getAccountBalance_ok() {
        account.updateAccountBalance("USD", 12L);
        assertEquals(12, account.getAccountBalance().get("USD"));
    }

    @Test
    void updateAccountBalance_negativeBalance() {
        Throwable thrown = assertThrows(IllegalArgumentException.class,
                () -> new Account("Иван").updateAccountBalance("USD", -12L));
        assertEquals("Колличество валюты не может быть отрицательным.", thrown.getMessage());
    }

    @Test
    void updateAccountBalance_notFoundValuta() {
        Throwable thrown = assertThrows(IllegalArgumentException.class,
                () -> account.updateAccountBalance("TUGRIC", 12L));
        assertEquals("No enum constant Account.Valuta.TUGRIC", thrown.getMessage());
    }

    @Test
    void updateAccountBalance_nullValuta() {
        Throwable thrown = assertThrows(NullPointerException.class,
                () -> account.updateAccountBalance(null, 12L));
        assertEquals("Валюта или Баланс не можут быть null.", thrown.getMessage());
    }

    @Test
    void updateAccountBalance_nullBalance() {
        Throwable thrown = assertThrows(NullPointerException.class,
                () -> account.updateAccountBalance("USD", null));
        assertEquals("Валюта или Баланс не можут быть null.", thrown.getMessage());
    }

    @Test
    void updateAccountBalance_nullValutaAndBalance() {
        Throwable thrown = assertThrows(NullPointerException.class,
                () -> account.updateAccountBalance(null, null));
        assertEquals("Валюта или Баланс не можут быть null.", thrown.getMessage());
    }

    @Test
    void updateAccountBalance_ok() {
        account.updateAccountBalance("USD", 12L);
        assertEquals(12, account.getAccountBalance().get("USD"));
    }

    @Test
    void undo_ok() {
        account.updateAccountBalance("RUB", 12L);
        account.setName("Иван");
        account.updateAccountBalance("USD", 12L);
        account.setName("Иван Иванович");
        account.updateAccountBalance("TRY", 12L);
        account.setName("Иван Иванович Иванов");
        account.updateAccountBalance("TRY", 15L);
        assertEquals("Иван Иванович Иванов", account.getName());
        assertEquals(12L, account.getAccountBalance().get("USD"));
        assertEquals(15L, account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Иван Иванович Иванов", account.getName());
        assertEquals(12L, account.getAccountBalance().get("USD"));
        assertEquals(12L, account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Иван Иванович", account.getName());
        assertEquals(12L, account.getAccountBalance().get("USD"));
        assertEquals(12L, account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Иван Иванович", account.getName());
        assertEquals(12L, account.getAccountBalance().get("USD"));
        assertNull(account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Иван", account.getName());
        assertEquals(12L, account.getAccountBalance().get("USD"));
        assertNull(account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Иван", account.getName());
        assertNull(account.getAccountBalance().get("USD"));
        assertNull(account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Валера", account.getName());
        assertNull(account.getAccountBalance().get("USD"));
        assertNull(account.getAccountBalance().get("TRY"));
        assertEquals(12L, account.getAccountBalance().get("RUB"));
        account.undo();
        assertEquals("Валера", account.getName());
        assertNull(account.getAccountBalance().get("USD"));
        assertNull(account.getAccountBalance().get("TRY"));
        assertNull(account.getAccountBalance().get("RUB"));

        Throwable thrown = assertThrows(NoSuchElementException.class, () -> account.undo());

        assertEquals(String.format("Изменений банковского счета открытого на имя %s - нет.", "Валера"), thrown.getMessage());
    }

    @Test
    @DisplayName("Изменение основного объекта не оказывает влияние на копию")
    void save_refactorAccountNotRefactorAccountSave() {
        account.updateAccountBalance("USD", 12L);
        Account accountActual = account.save();
        assertNotEquals(account, accountActual);
        assertEquals(account.getName(), accountActual.getName());
        assertEquals(account.getAccountBalance().size(), accountActual.getAccountBalance().size());
        account.setName("Вовчик");
        account.updateAccountBalance("RUB", 12L);
        assertNotEquals(account.getName(), accountActual.getName());
        assertNotEquals(account.getAccountBalance().size(), accountActual.getAccountBalance().size());
    }

    @Test
    void save_immutableAccountSave() {
        Account accountActual = account.save();
        assertNotEquals(account, accountActual);
        assertThrows(NullPointerException.class, () -> accountActual.setName("Давид"));
    }

    @Test
    void save_ok() {
        Account accountActual = account.save();
        assertNotEquals(account, accountActual);
        assertEquals(account.getName(), accountActual.getName());
        assertEquals(account.getAccountBalance().size(), accountActual.getAccountBalance().size());
    }
}