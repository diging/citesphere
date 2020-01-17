package edu.asu.diging.citesphere.core.repository;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

public class IdGenerator extends SequenceStyleGenerator implements IdentifierGenerator, Configurable {
	
	private String prefix;
	private String numberFormat;

	@Override
	public void configure(Type type, Properties properties, ServiceRegistry sr) throws MappingException {
		super.configure(LongType.INSTANCE, properties, sr);
		prefix = properties.getProperty("prefix");
        numberFormat = "%d";
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
	    return prefix + String.format(numberFormat, super.generate(session, obj));
	}

}