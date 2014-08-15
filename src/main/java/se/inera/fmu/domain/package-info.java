/**
 *
 * The domain model, services and repository interfaces. This is the central part of the
 * application. The ubiquitous language is used in classes, interfaces and method signatures,
 * and every concept in here is familiar to a expert in the fmu domain.
 *
 * There is no infrastructure or user interface related code here, except for things like
 * transactional and security metadata which is likely to be relevant to a domain expert
 * ("Either all of foo succeeds or none of it does", "In order to do bar you need to be a Supervisor", and so on).
 */
package se.inera.fmu.domain;
