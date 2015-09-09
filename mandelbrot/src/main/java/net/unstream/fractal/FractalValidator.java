package net.unstream.fractal;

import net.unstream.fractal.api.domain.Fractal;
import net.unstream.polynomial.PolynomialFunction;
import net.unstream.polynomial.PolynomialParseException;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FractalValidator implements Validator {

    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Fractal.class.equals(clazz);
    }

	@Override
    public void validate(Object obj, Errors error) {
        Fractal f = (Fractal) obj;
        if (f.isRenderJulia() && f.getPolynomial() != "") {
        	try {
				new PolynomialFunction(f.getPolynomial());
			} catch (PolynomialParseException e) {
				error.rejectValue("polynomial", null , e.getMessage());
			}
        }
    }

}