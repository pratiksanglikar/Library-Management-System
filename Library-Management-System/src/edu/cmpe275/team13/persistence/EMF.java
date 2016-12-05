package edu.cmpe275.team13.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
    private static final EntityManagerFactory emfInstance = Persistence
            .createEntityManagerFactory("cmpe275-term-project");

        private EMF() {
        }

        public static EntityManagerFactory get() {
            return emfInstance;
        }
}