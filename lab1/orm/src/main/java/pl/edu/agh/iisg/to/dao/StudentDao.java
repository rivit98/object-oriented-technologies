package pl.edu.agh.iisg.to.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;

import javax.persistence.PersistenceException;

public class StudentDao extends GenericDao<Student> {

    public Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        try {
            save(new Student(firstName, lastName, indexNumber));
            return findByIndexNumber(indexNumber);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Student> findByIndexNumber(final int indexNumber) {
        try {
            Student s = currentSession()
                    .createQuery("SELECT s FROM Student s WHERE s.indexNumber = :indexNumber", Student.class)
                    .setParameter("indexNumber", indexNumber).getSingleResult();
            return Optional.of(s);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Map<Course, Float> createReport(final Student student) {
        var courseGradeMap = student.gradeSet().stream()
                .collect(Collectors.groupingBy(Grade::course, Collectors.toList()));

        Map<Course, Float> report = new HashMap<>();
        courseGradeMap.forEach((k, v) -> {
            report.put(k, (float) v.stream().mapToDouble(Grade::grade).average().getAsDouble());
        });

        return report;
    }

}
