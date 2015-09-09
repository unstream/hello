/**
 * Define a grammar for complex polynomial expressions like c^4, 4, 3.2c + 4.5
 */
grammar Polynomial;

@header { 
package net.unstream.polynomial; 
import java.util.TreeMap;
import java.util.Map;
import org.apache.commons.math3.complex.Complex;
}
@members {
	public Map<Integer, Complex> map = new TreeMap<Integer, Complex>();
	
	private void addExponent(final int exponent, final Complex coeff) {
		Complex oldCoeff = map.get(exponent);
		if ( oldCoeff == null) {
			map.put(exponent, coeff);
		} else{ 
			map.put(exponent, coeff.add(oldCoeff));
		}
	}
}

/**
 * 3 + (3 + 5i) c^5 + 6c^3
 */		

polynomial
	:	a1 = atomPolynomial 		{ addExponent($a1.exp, $a1.coeff); }
		( '+' a2 = atomPolynomial	{ addExponent($a2.exp, $a2.coeff); }
		| '-' a2 = atomPolynomial	{ addExponent($a2.exp, $a2.coeff.negate()); }
		)*
	;

atomPolynomial returns [int exp, Complex coeff] 
	: t = term 		{ $exp = $t.exp; $coeff = $t.coeff; }
	| '+' t = term	{ $exp = $t.exp; $coeff = $t.coeff; }
	| '-' t = term 	{ $exp = $t.exp; $coeff = $t.coeff.negate(); }
	;

/**
 * 3, (3 + 5i) c^5, 6c^3
 */		
term returns [int exp, Complex coeff]
	: complex 				{$exp = 0; $coeff = $complex.value;}
	| power					{$coeff = new Complex(1,0); $exp = $power.value;}												
	| c=complex p=power 	{$coeff = $complex.value; $exp = $power.value;}
;

/**
 * c, c^4 
 */
power returns [int value]
	: VAR 					{$value = 1;}
	| VAR '^' exp=DIGIT+ 	{$value = Integer.parseInt($exp.text);}
	; 

complex returns [Complex value]
    :    a1=atomExp 		{$value = $a1.value;} 
         ( '+' a2=atomExp 	{$value = $a1.value.add($a2.value);}
         | '-' a2=atomExp 	{$value = $a1.value.subtract($a2.value);}
         ) *
    ;

atomExp returns [Complex value]
    :   r=real 			{$value = new Complex(Double.parseDouble($r.text), 0);} 
    |	(i=real) I 		{$value = new Complex(0, Double.parseDouble($i.text));} 
    |	I				{$value = new Complex(0, 1);} 
    |   '(' c=complex ')' 	{$value = $c.value;}
    ;

real
	: n = DIGIT+(DOT DIGIT+)? 
	;

I : 'i'|'I' ;
VAR : 'c' ;
DIGIT : ('0'..'9') ;
DOT : '.' ;
WS : 	[ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
