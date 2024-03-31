package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.ITeacherDAO;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.service.util.JPAHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Provider
@ApplicationScoped
public class TeacherServiceImp implements ITeacherService{

    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceImp.class);

    @Inject
    private ITeacherDAO teacherDAO;


    @Override
    public Teacher insertTeacher(TeacherInsertDTO teacherInsertDTO) throws Exception {
        Teacher teacher = null;

        try {
            JPAHelper.beginTransaction();
            teacher = Mapper.mapToTeacher(teacherInsertDTO);
            teacher = teacherDAO.insert(teacher);

            if (teacher.getId() == null) {
                throw new Exception("Insert Error");
            }

            JPAHelper.commitTransaction();
            logger.info("Teacher with id " + teacher.getId() + " was inserted");
        }catch (Exception e) {
            JPAHelper.rollBackTransaction();
            logger.error("Teacher not inserted" + e.getMessage());
            throw e;

        }finally {
            JPAHelper.closeEntityManager();
        }
        return teacher;
    }

    @Override
    public Teacher updateTeacher(TeacherUpdateDTO teacherUpdateDTO) throws EntityNotFoundException {
        Teacher teacherToUpdate = null;
        Teacher updatedTeacher = null;

        try {
            JPAHelper.beginTransaction();
            Optional.ofNullable(teacherDAO.getById(teacherUpdateDTO.getId()))
                    .orElseThrow(() -> new EntityNotFoundException(Teacher.class, teacherUpdateDTO.getId()));

            teacherToUpdate = Mapper.mapToTeacher(teacherUpdateDTO);
            updatedTeacher = teacherDAO.update(teacherToUpdate);
            JPAHelper.commitTransaction();
            logger.info("Teacher with id " + updatedTeacher.getId() + " was updated");
        }catch (EntityNotFoundException e) {
            JPAHelper.rollBackTransaction();
            logger.error("Teacher not found" + e.getMessage());
            throw e;

        }finally {
            JPAHelper.closeEntityManager();
        }
        return updatedTeacher;
    }

    @Override
    public void deleteTeacher(Long id) throws EntityNotFoundException {
        try {
            JPAHelper.beginTransaction();
            Optional.ofNullable(teacherDAO.getById(id)).orElseThrow(() -> new EntityNotFoundException(Teacher.class, id));
            teacherDAO.delete(id);
            JPAHelper.commitTransaction();
            logger.info("Teacher with id " + id + "was deleted");
        }catch (EntityNotFoundException e) {
            JPAHelper.rollBackTransaction();
            logger.warn("Teacher not found" + e.getMessage());
            throw e;

        }finally {
            JPAHelper.closeEntityManager();
        }
    }

    @Override
    public List<Teacher> getTeacherByLastname(String lastname) throws EntityNotFoundException {
        List<Teacher> teachers;

        try {
            JPAHelper.beginTransaction();
            teachers = Optional.of(teacherDAO.getByLastName(lastname))
                    .orElseThrow(() -> new EntityNotFoundException(List.class, 0L));
            JPAHelper.commitTransaction();
            logger.info("Teachers were found");
        }catch (EntityNotFoundException e) {
            JPAHelper.rollBackTransaction();
            logger.warn("Warning - teachers not found " + e.getMessage());
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return teachers;
    }

    @Override
    public Teacher getTeacherById(Long id) throws EntityNotFoundException {
        Teacher teacher = null;

        try {
            JPAHelper.beginTransaction();
            teacher = Optional.ofNullable(teacherDAO.getById(id))
                    .orElseThrow(() -> new EntityNotFoundException(Teacher.class, id));
            JPAHelper.commitTransaction();
            logger.info("Teacher with id" + id + " was found");
        }catch (EntityNotFoundException e) {
            JPAHelper.rollBackTransaction();
            logger.warn("Warning teacher with id " + id + " was not found " + e.getMessage());
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return teacher;
    }
}
