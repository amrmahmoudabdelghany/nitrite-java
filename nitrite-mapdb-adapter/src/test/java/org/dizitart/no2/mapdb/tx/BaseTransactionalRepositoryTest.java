package org.dizitart.no2.mapdb.tx;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.mapdb.MapDBModule;
import org.dizitart.no2.mapdb.MapDBModuleBuilder;
import org.dizitart.no2.mapdb.repository.data.*;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.repository.TransactionalRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.dizitart.no2.filters.Filter.ALL;
import static org.dizitart.no2.mapdb.DbTestOperations.getRandomTempDbFile;

/**
 * @author Anindya Chatterjee
 */
@RunWith(value = Parameterized.class)
public abstract class BaseTransactionalRepositoryTest {
    @Parameterized.Parameter
    public boolean inMemory = false;
    @Parameterized.Parameter(value = 1)
    public boolean isProtected = false;

    protected Nitrite db;
    ObjectRepository<Company> companyRepository;
    TransactionalRepository<Company> txCompanyRepository;
    ObjectRepository<Employee> employeeRepository;
    TransactionalRepository<Employee> txEmployeeRepository;

    ObjectRepository<ClassA> aObjectRepository;
    ObjectRepository<ClassC> cObjectRepository;
    private final String fileName = getRandomTempDbFile();

    @Parameterized.Parameters(name = "InMemory = {0}, Protected = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {false, false},
            {false, true},
            {true, false},
            {true, true},
        });
    }

    @Before
    public void setUp() {
        openDb();

        companyRepository = db.getRepository(Company.class);
        employeeRepository = db.getRepository(Employee.class);

        txCompanyRepository = companyRepository.beginTransaction();
        txEmployeeRepository = employeeRepository.beginTransaction();

        aObjectRepository = db.getRepository(ClassA.class);
        cObjectRepository = db.getRepository(ClassC.class);

        for (int i = 0; i < 10; i++) {
            Company company = DataGenerator.generateCompanyRecord();
            txCompanyRepository.insert(company);
            Employee employee = DataGenerator.generateEmployee();
            employee.setEmpId((long) i + 1);
            txEmployeeRepository.insert(employee);

            aObjectRepository.insert(ClassA.create(i + 50));
            cObjectRepository.insert(ClassC.create(i + 30));
        }
    }

    private void openDb() {
        MapDBModuleBuilder builder = MapDBModule.withConfig();

        if (!inMemory) {
            builder.filePath(fileName);
        }

        MapDBModule storeModule = builder.build();
        NitriteBuilder nitriteBuilder = Nitrite.builder()
            .fieldSeparator(".")
            .loadModule(storeModule);

        if (isProtected) {
            db = nitriteBuilder.openOrCreate("test-user", "test-password");
        } else {
            db = nitriteBuilder.openOrCreate();
        }
    }

    @After
    public void clear() throws IOException {
        txEmployeeRepository.close();
        txCompanyRepository.close();

        if (companyRepository != null && !companyRepository.isDropped()) {
            companyRepository.remove(ALL);
        }

        if (employeeRepository != null && !employeeRepository.isDropped()) {
            employeeRepository.remove(ALL);
        }

        if (aObjectRepository != null && !aObjectRepository.isDropped()) {
            aObjectRepository.remove(ALL);
        }

        if (cObjectRepository != null && !cObjectRepository.isDropped()) {
            cObjectRepository.remove(ALL);
        }

        if (db != null && !db.isClosed()) {
            db.commit();
            db.close();
        }

        if (!inMemory) {
            Files.delete(Paths.get(fileName));
        }
    }
}
