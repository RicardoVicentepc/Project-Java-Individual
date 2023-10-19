import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import entities.Computer;
import entities.Cpu;
import entities.Disk;
import entities.Statistics;
import oshi.SystemInfo;
import repositories.*;
import utils.Util;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final ComputerRepository COMPUTER_REPOSITORY = new ComputerRepository(new ConnectionMySql());
    private static final CpuRepository CPU_REPOSITORY = new CpuRepository(new ConnectionMySql());
    private static final DiskRepository DISK_REPOSITORY = new DiskRepository(new ConnectionMySql());
    private static final StatisticsRepository STATISTICS_REPOSITORY = new StatisticsRepository(new ConnectionMySql());

    private static Computer computer = new Computer(1);
    private static Looca looca;
    private static Scanner scanner;

    public static void main(String[] args) {
        setup();

        scanner = new Scanner(System.in);
        looca = new Looca();

        boolean running = true;

        while (running) {
            printMenu();
            int option = getUserOption();

            switch (option) {
                case 1:
                    showDiskCapacity();
                    break;
                case 2:
                    showCpuUsage();
                    break;
                case 3:
                    showRamUsage();
                    break;
                case 0:
                    running = false;
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("Selecione uma opção:");
        System.out.println("1. Capacidade do disco rígido");
        System.out.println("2. Uso da CPU");
        System.out.println("3. Uso da RAM");
        System.out.println("0. Sair");
    }

    private static int getUserOption() {
        System.out.print("Opção: ");
        return scanner.nextInt();
    }

    private static void showDiskCapacity() {
        double diskCapacity = looca.getGrupoDeDiscos().getTamanhoTotal().doubleValue();
        System.out.println("Capacidade do disco rígido: " + diskCapacity + " bytes");
    }

    private static void showCpuUsage() {
        double cpuUsage = looca.getProcessador().getUso();
        System.out.println("Uso da CPU: " + cpuUsage + "%");
    }

    private static void showRamUsage() {
        double ramUsage = looca.getMemoria().getEmUso().doubleValue();
        System.out.println("Uso da RAM: " + ramUsage + " bytes");
    }

    private static void setup() {
        looca = new Looca();
        Computer computer = new Computer();
        Cpu cpu = new Cpu(looca.getProcessador().getId(), looca.getProcessador().getNome());
        Disk disk = new Disk();
        for (Disco d : looca.getGrupoDeDiscos().getDiscos()) {
            disk.setModel(d.getModelo());
            disk.setId(d.getSerial());
        }

        computer.setHostname(looca.getRede().getParametros().getHostName());
        computer.setSystemInfo(looca.getSistema().getSistemaOperacional());
        computer.setMaker(looca.getSistema().getFabricante());
        computer.setDisk(disk);
        computer.setCpu(cpu);

        DISK_REPOSITORY.save(disk);
        CPU_REPOSITORY.save(cpu);
        COMPUTER_REPOSITORY.save(computer);
        Application.computer = computer;
    }
}