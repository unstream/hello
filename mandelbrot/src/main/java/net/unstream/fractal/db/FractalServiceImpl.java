package net.unstream.fractal.db;

import java.util.ArrayList;
import java.util.List;

import net.unstream.fractal.api.FractalService;
import net.unstream.fractal.api.FractalServiceException;
import net.unstream.fractal.api.domain.Fractal;
import net.unstream.fractal.api.domain.Image;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FractalServiceImpl implements FractalService {

	@Autowired private GraphDatabase graphDatabase;
	@Autowired private FractalRepository fractalRepository;
	@Autowired private  ImageRepository imageRepository;
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findAll()
	 */
	@Override
	@Transactional
	public List<Fractal> findAll() {
		Transaction tx = graphDatabase.beginTx();
		try {
	    	List<Fractal> fractals = new ArrayList<Fractal>();
				Iterable<Fractal> iterable = fractalRepository.findAll();
				for (Fractal fractal : iterable) {
					fractals.add(fractal);
				}
			return fractals;
		} catch (Exception e) {
			tx.failure();
			throw new FractalServiceException(e);
		} finally {
			tx.success();
			tx.close();
		}
		
	}

	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#save(net.unstream.fractal.api.domain.Fractal)
	 */
	@Override
	@Transactional
	public Fractal save(Fractal fractal) {
		Transaction tx = graphDatabase.beginTx();
    	try {
    		return fractalRepository.save(fractal);
    	} catch (Exception e) {
    		tx.failure();
    		throw new FractalServiceException(e);
		} finally {
    		tx.success();
			tx.close();
		}
	}

	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findById(long)
	 */
    @Override
	@Transactional(readOnly=true)
	public Fractal findById(long id) {
	  	Transaction tx = graphDatabase.beginTx();
    	try {
			return fractalRepository.findById(id);
		} catch (Exception e) {
			tx.failure();
			throw new FractalServiceException(e);
		} finally {
			tx.success();
			tx.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findImageById(long)
	 */
    @Override
	@Transactional(readOnly=true)
	public Image findImageById(long id) {
	  	Transaction tx = graphDatabase.beginTx();
    	try {
			return imageRepository.findById(id);
		} catch (Exception e) {
			tx.failure();
			throw new FractalServiceException(e);
		} finally {
			tx.success();
			tx.close();
		}
	}
    /* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#delete(java.lang.Long)
	 */
    @Override
	@Transactional
	public void delete(Long id) {
    	Transaction tx = graphDatabase.beginTx();
    	try {
        	fractalRepository.delete(id);
    	} catch (Exception e) {
    		tx.failure();
    		throw new FractalServiceException(e);
    	} finally {
    		tx.success();
    		tx.close();
    	} 
	}

}
