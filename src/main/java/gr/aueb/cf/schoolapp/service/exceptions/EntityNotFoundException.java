package gr.aueb.cf.schoolapp.service.exceptions;

public class EntityNotFoundException extends Exception {

    private static final Long serialVersionUID = 123455L;

    public EntityNotFoundException(Class<?> clazz, Long id) {
        super("Entity " + clazz.getSimpleName() + "with id " + id + " does not exist");
    }
}
