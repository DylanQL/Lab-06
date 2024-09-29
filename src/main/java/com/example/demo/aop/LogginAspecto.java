package com.example.demo.aop;

import com.example.demo.domain.entities.Alumno;
import com.example.demo.domain.entities.Auditoria;
import com.example.demo.domain.entities.Curso;
import com.example.demo.domain.persistence.AuditoriaDao;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Calendar;

///////


@Component
@Aspect
public class LogginAspecto {

    private Long tx;

    @Autowired
    private AuditoriaDao auditoriaDao;

    @Around("execution(* com.example.demo.services.*ServiceImpl.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result =  null;
        Long currTime = System.currentTimeMillis();
        tx = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = "tx[" + tx + "] - " + joinPoint.getSignature().getName();
        if(joinPoint.getArgs().length > 0)
            logger.info(metodo + "() INPUT:" + Arrays.toString(joinPoint.getArgs()));
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        logger.info(metodo + "(): tiempo transcurrido " + (System.currentTimeMillis() - currTime) + " ms.");
        return result;
    }

    @After("execution(* com.example.demo.controllers.*Controller.guardar*(..)) ||" +
            "execution(* com.example.demo.controllers.*Controller.editar*(..)) ||" +
            "execution(* com.example.demo.controllers.*Controller.eliminar*(..))")
    public void auditoria(JoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = joinPoint.getSignature().getName();
        Integer id = null;
        String tabla = null;

        if(metodo.startsWith("guardar")){
            Object[] parametros = joinPoint.getArgs();
            if (parametros[0] instanceof Curso) {
                Curso curso = (Curso) parametros[0];
                id = curso.getId();
                tabla = "cursos";
            } else if (parametros[0] instanceof Alumno) {
                Alumno alumno = (Alumno) parametros[0];
                id = alumno.getId();
                tabla = "alumnos";
            }
        } else if(metodo.startsWith("editar")){
            Object[] parametros = joinPoint.getArgs();
            id = (Integer)parametros[0];
            tabla = "cursos"; // Asumimos que es un curso, pero deberías verificar si es un alumno
        } else if(metodo.startsWith("eliminar")){
            Object[] parametros = joinPoint.getArgs();
            id = (Integer)parametros[0];
            tabla = "cursos"; // Asumimos que es un curso, pero deberías verificar si es un alumno
        }

        String traza = "tx[" + tx + "] - " + metodo;
        logger.info(traza + "(): registrando auditoria...");
        auditoriaDao.save(new Auditoria(tabla, id, Calendar.getInstance().getTime(),
                "usuario", metodo));
    }
}