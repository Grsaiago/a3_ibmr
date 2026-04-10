import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

enum Operation {
    EXIT,
    REGISTER_DEFAULT_EMPLOYEE,
    REGISTER_COMMISSIONED_EMPLOYEE,
    REGISTER_PRODUCTIVITY_EMPLOYEE,
    GENERATE_PAYMENT_SHEET;

    public static Operation from(int id) {
        Operation[] values = Operation.values();
        if (id >= 1 && id <= values.length) {
            return values[id - 1];
        }
        return null;
    }
}

interface ICalculateSalary {
    double calculateSalary();
}

abstract class AEmployee implements ICalculateSalary {

    final int BASE_SALARY = 2_000;

    protected String name;
    protected String registry;

    protected AEmployee(String name, String registry) {
        this.name = name;
        this.registry = registry;
    }

    public String getName() {
        return name;
    }

    public String getRegistry() {
        return registry;
    }
}

class DefaultEmployee extends AEmployee {

    public DefaultEmployee(String name, String registry) {
        super(name, registry);
    }

    @Override
    public double calculateSalary() {
        return this.BASE_SALARY;
    }

    @Override
    public String toString() {
        return String.format(
            "Nome: %s\nMatrícula: %s\nSalário Fixo: %.1f, Extras: 0.0\nSalário final: %.1f",
            this.name,
            this.registry,
            (float) this.BASE_SALARY,
            (float) this.BASE_SALARY
        );
    }
}

class CommissionedEmployee extends AEmployee {

    private float commission;

    public CommissionedEmployee(
        String name,
        String registry,
        float commission
    ) {
        super(name, registry);
        this.commission = commission;
    }

    @Override
    public double calculateSalary() {
        return this.BASE_SALARY + this.commission;
    }

    @Override
    public String toString() {
        return String.format(
            "Nome: %s\nMatrícula: %s\nSalário Fixo: %.1f, Comissão: %.1f\nSalário final: %.1f",
            this.name,
            this.registry,
            this.BASE_SALARY,
            this.commission,
            this.calculateSalary()
        );
    }
}

class ProductivityEmployee extends AEmployee {

    Float productivity;

    public ProductivityEmployee(
        String name,
        String registry,
        Float productivity
    ) {
        super(name, registry);
        this.productivity = productivity;
    }

    @Override
    public double calculateSalary() {
        return this.BASE_SALARY + this.productivity;
    }

    @Override
    public String toString() {
        return String.format(
            "Nome: %s\nMatrícula: %s\nSalário Fixo: %.1f, Produtividade: %.1f\nSalário final: %.1f",
            this.name,
            this.registry,
            this.BASE_SALARY,
            this.productivity,
            this.calculateSalary()
        );
    }
}

class EmployeeRegistry {

    private ArrayList<AEmployee> _registry;

    public EmployeeRegistry() {
        this._registry = new ArrayList<>(10);
    }

    public void addEmployee(AEmployee employee) {
        this._registry.add(employee);
    }

    public void printSalaries() {
        for (AEmployee employee : this._registry) {
            System.out.println("## -------- ##");
            System.out.println(employee);
            System.out.println("## -------- ##");
        }
    }
}

class REPL {

    private final String USAGE_MESSAGE =
        "Escolha uma operação:\n" +
        "1. Sair\n" +
        "2. Registrar um funcionário padrão\n" +
        "3. Registrar um funcionário comissionado\n" +
        "4. Registrar um funcionário de produtividade\n" +
        "5. Mostrar folha de pagamento\n";

    private final Scanner sc;
    private final EmployeeRegistry registry;
    private final PrintStream out;

    public REPL(InputStream readSource, PrintStream outStream) {
        this.out = outStream;
        this.sc = new Scanner(readSource);
        this.registry = new EmployeeRegistry();
    }

    private Operation readOperation() {
        while (true) {
            this.printUsage();
            int chosenOperation = sc.nextInt();
            this.sc.nextLine();
            if (
                !Arrays.stream(Operation.values()).anyMatch(
                    op -> (op.ordinal() + 1) == chosenOperation
                )
            ) {
                System.out.println(
                    "Operação invalida, tente uma das operações listadas"
                );
                continue;
            }
            return Operation.from(chosenOperation);
        }
    }

    private void dispatchOperation(Operation op) {
        switch (op) {
            case EXIT:
                System.out.println("Adeus!");
                this.sc.close();
                System.exit(0);
                return;
            case REGISTER_DEFAULT_EMPLOYEE:
                this.readDefaultEmployee();
                break;
            case REGISTER_COMMISSIONED_EMPLOYEE:
                this.readCommissionedEmployee();
                break;
            case REGISTER_PRODUCTIVITY_EMPLOYEE:
                this.readProductivityEmployee();
                break;
            case GENERATE_PAYMENT_SHEET:
                this.registry.printSalaries();
                break;
        }
    }

    private void readDefaultEmployee() {
        Boolean valid = false;
        while (!valid) {
            this.out.println("Insira o nome do funcionário:");
            String name = this.sc.nextLine();
            this.out.println("Insira o identificador do funcionário:");
            String registry = this.sc.nextLine();
            try {
                this.registry.addEmployee(new DefaultEmployee(name, registry));
                valid = true;
            } catch (IllegalArgumentException e) {
                this.out.println(
                    "Argumentos inválidos para criação do funcionário padrão. Por favor, tente novamente."
                );
            }
        }
    }

    private void readCommissionedEmployee() {
        Boolean valid = false;
        while (!valid) {
            this.out.println("Insira o nome do funcionário:");
            String name = this.sc.nextLine();
            this.out.println("Insira o identificador do funcionário:");
            String registry = this.sc.nextLine();
            this.out.println("Insira a quantidade de comissão deste funcionário:");
            Float commission = this.sc.nextFloat();
            this.sc.nextLine();
            try {
                this.registry.addEmployee(
                    new CommissionedEmployee(name, registry, commission)
                );
                valid = true;
            } catch (IllegalArgumentException e) {
                this.out.println(
                    "Argumentos inválidos para criação do funcionário comissionado. Por favor, tente novamente."
                );
            }
        }
    }

    private void readProductivityEmployee() {
        boolean valid = false;
        while (!valid) {
            this.out.println("Insira o nome do funcionário:");
            String name = this.sc.nextLine();
            this.out.println("Insira o identificador do funcionário:");
            String registry = this.sc.nextLine();
            this.out.println("Insira a produtividade do funcionário:");
            Float productivity = this.sc.nextFloat();
            this.sc.nextLine();
            try {
                this.registry.addEmployee(
                    new ProductivityEmployee(name, registry, productivity)
                );
                valid = true;
            } catch (IllegalArgumentException e) {
                this.out.println(
                    "Argumentos inválidos para criação do funcionário de produtividade. Por favor, tente novamente."
                );
            }
        }
    }

    public void run() {
        while (true) {
            Operation operation = this.readOperation();
            this.dispatchOperation(operation);
        }
    }

    private void printUsage() {
        this.out.println(USAGE_MESSAGE);
    }
}

public class App {
    public static void main(String[] args) {
        REPL app = new REPL(System.in, System.out);
        app.run();
    }
}
