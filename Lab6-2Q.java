The problem at hand simulates a coffee shop scenario where multiple tasks interact with each other. The tasks include baristas preparing coffee (producers), customers picking up coffee (consumers), and a coffee reviewer (observer) sampling the coffee.

class CounterEmptyException extends Exception {
    public CounterEmptyException(String message) {
        super(message);
    }
}

class CoffeeCounter {
    private int counter = 0;
    private final int maxCapacity;
    
    public CoffeeCounter(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    // Method to prepare coffee (Barista)
    public synchronized void prepareCoffee(int baristaId) {
        try {
            while (counter == maxCapacity) {
                System.out.println("Barista " + baristaId + " is waiting. Counter is full.");
                wait();
            }
            counter++;
            System.out.println("Barista " + baristaId + " prepared coffee. Counter: " + counter);
            notifyAll(); // Notify all waiting threads (customers and reviewer)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to pick up coffee (Customer)
    public synchronized void pickCoffee(int customerId) throws CounterEmptyException {
        try {
            while (counter == 0) {
                System.out.println("Customer " + customerId + " is waiting. Counter is empty.");
                wait();
            }
            counter--;
            System.out.println("Customer " + customerId + " picked up coffee. Counter: " + counter);
            notifyAll(); // Notify all waiting threads (baristas and reviewer)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to sample coffee (Reviewer)
    public synchronized void sampleCoffee() throws CounterEmptyException {
        try {
            while (counter == 0) {
                System.out.println("Coffee Reviewer is waiting. Counter is empty.");
                wait();
            }
            counter--;
            System.out.println("Coffee Reviewer sampled coffee. Counter: " + counter);
            notifyAll(); // Notify all waiting threads (baristas and customers)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Barista implements Runnable {
    private final CoffeeCounter coffeeCounter;
    private final int baristaId;
    private final int numCoffees;

    public Barista(int baristaId, CoffeeCounter coffeeCounter, int numCoffees) {
        this.baristaId = baristaId;
        this.coffeeCounter = coffeeCounter;
        this.numCoffees = numCoffees;
    }

    @Override
    public void run() {
        for (int i = 0; i < numCoffees; i++) {
            coffeeCounter.prepareCoffee(baristaId);
            try {
                Thread.sleep(1000); // Simulate time taken to prepare coffee
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Customer implements Runnable {
    private final CoffeeCounter coffeeCounter;
    private final int customerId;
    private final int numCoffees;

    public Customer(int customerId, CoffeeCounter coffeeCounter, int numCoffees) {
        this.customerId = customerId;
        this.coffeeCounter = coffeeCounter;
        this.numCoffees = numCoffees;
    }

    @Override
    public void run() {
        for (int i = 0; i < numCoffees; i++) {
            try {
                coffeeCounter.pickCoffee(customerId);
                Thread.sleep(1000); // Simulate time taken to pick coffee
            } catch (InterruptedException | CounterEmptyException e) {
                e.printStackTrace();
            }
        }
    }
}

class CoffeeReviewer implements Runnable {
    private final CoffeeCounter coffeeCounter;

    public CoffeeReviewer(CoffeeCounter coffeeCounter) {
        this.coffeeCounter = coffeeCounter;
    }

    @Override
    public void run() {
        while (true) {
            try {
                coffeeCounter.sampleCoffee();
                Thread.sleep(2000); // Simulate time taken for sampling coffee
            } catch (InterruptedException | CounterEmptyException e) {
                e.printStackTrace();
            }
        }
    }
}

public class CoffeeShopSimulation {

    public static void main(String[] args) {
        CoffeeCounter coffeeCounter = new CoffeeCounter(3);

        // Baristas' tasks (producers)
        Thread barista1 = new Thread(new Barista(1, coffeeCounter, 2));
        Thread barista2 = new Thread(new Barista(2, coffeeCounter, 3));

        // Customers' tasks (consumers)
        Thread customer1 = new Thread(new Customer(1, coffeeCounter, 1));
        Thread customer2 = new Thread(new Customer(2, coffeeCounter, 2));
        Thread customer3 = new Thread(new Customer(3, coffeeCounter, 1));

        // Coffee Reviewer task (observer)
        Thread reviewer = new Thread(new CoffeeReviewer(coffeeCounter));

        // Start all threads
        barista1.start();
        barista2.start();
        customer1.start();
        customer2.start();
        customer3.start();
        reviewer.start();
    }
}
