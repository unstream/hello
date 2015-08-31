package net.unstream.fractal.db;

import java.util.ArrayList;
import java.util.List;

import net.unstream.fractal.api.FractalService;
import net.unstream.fractal.api.domain.Fractal;
import net.unstream.fractal.api.domain.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FractalServiceImpl implements FractalService {

	@Lazy @Autowired private GraphDatabase graphDatabase;
	@Autowired private FractalRepository fractalRepository;
	@Autowired private  ImageRepository imageRepository;
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findAll()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<Fractal> findAll() {
    	List<Fractal> fractals = new ArrayList<Fractal>();
			Iterable<Fractal> iterable = fractalRepository.findAll();
			for (Fractal fractal : iterable) {
				fractals.add(fractal);
			}
		return fractals;
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findAll()
	 */
	@Override
	@Transactional(readOnly=true)
	public Page<Fractal> findAll(Pageable pageable) {
		Page<Fractal> page = fractalRepository.findAll(pageable);
		return page;
	}

	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#save(net.unstream.fractal.api.domain.Fractal)
	 */
	@Override
	@Transactional
	public Fractal save(Fractal fractal) {
		return fractalRepository.save(fractal);
	}

	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findById(long)
	 */
    @Override
	@Transactional(readOnly=true)
	public Fractal findById(long id) {
		return fractalRepository.findById(id);
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findImageById(long)
	 */
    @Override
	@Transactional(readOnly=true)
	public Image findImageById(long id) {
		return imageRepository.findById(id);
	}

    /* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#delete(java.lang.Long)
	 */
    @Override
	@Transactional
	public void delete(Long id) {
       	fractalRepository.delete(id);
	}

}
