
import java.io.*;
import java.util.Scanner;

class Administrator {
    private String databasePath;
    private String eventsPath;

    public Administrator(String databasePath, String eventsPath) {
        this.databasePath = databasePath;
        this.eventsPath = eventsPath;
    }

    public void addCard(Scanner scanner) {
        System.out.print("Podaj numer karty: ");
        String cardNumber = scanner.nextLine();
        System.out.print("Podaj saldo: ");
        double balance = Double.parseDouble(scanner.nextLine());
        System.out.print("Podaj PIN: ");
        String pin = scanner.nextLine();
        System.out.print("Podaj typ karty: ");
        String cardType = scanner.nextLine();

        try (PrintWriter writer = new PrintWriter(new FileWriter(databasePath, true))) {
            writer.println(cardNumber + " " + balance + " " + pin + " " + cardType);
            System.out.println("Karta została dodana do bazy danych.");
        } catch (IOException e) {
            System.out.println("Błąd podczas dodawania karty do bazy danych.");
        }
    }

    public void removeCard(Scanner scanner) {
        System.out.print("Podaj numer karty: ");
        String cardNumber = scanner.nextLine();
        System.out.print("Podaj PIN: ");
        String pin = scanner.nextLine();

        try {
            File inputFile = new File(databasePath);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            boolean cardRemoved = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(" ");
                if (!data[0].equals(cardNumber) || !data[2].equals(pin)) {
                    writer.write(currentLine + "\n");
                } else {
                    cardRemoved = true;
                }
            }

            reader.close();
            writer.close();

            if (!inputFile.delete()) {
                System.out.println("Błąd podczas usuwania karty z bazy danych.");
                return;
            }

            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Błąd podczas usuwania karty z bazy danych.");
            } else {
                if (cardRemoved) {
                    System.out.println("Karta została usunięta z bazy danych.");
                } else {
                    System.out.println("Podany numer karty lub PIN jest nieprawidłowy.");
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas usuwania karty z bazy danych.");
        }
    }

    public void clearEventLog(Scanner scanner) {
        System.out.print("Czy na pewno chcesz wyczyścić plik zdarzeń? (tak/nie): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("tak")) {
            try (PrintWriter writer = new PrintWriter(eventsPath)) {
                System.out.println("Plik zdarzeń został wyczyszczony.");
            } catch (IOException e) {
                System.out.println("Błąd podczas czyszczenia pliku zdarzeń.");
            }
        } else {
            System.out.println("Operacja została anulowana.");
        }
    }
}
class AdminProgram {
    public static void main(String[] args) {
        Bankomat bankomat = new Bankomat("database.txt", "events.txt");
        Administrator administrator = new Administrator("database.txt", "events.txt");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Wprowadź polecenie (dodaj/usun/wyczysc/wyjdz): ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("dodaj")) {
                administrator.addCard(scanner);
            } else if (command.equalsIgnoreCase("usun")) {
                administrator.removeCard(scanner);
            } else if (command.equalsIgnoreCase("wyczysc")) {
                administrator.clearEventLog(scanner);
            } else if (command.equalsIgnoreCase("wyjdz")) {
                break;
            } else {
                System.out.println("Nieprawidłowe polecenie.");
            }

            System.out.println();
        }

        scanner.close();
    }
}


