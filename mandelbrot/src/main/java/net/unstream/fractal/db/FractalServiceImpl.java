package net.unstream.fractal.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.unstream.fractal.api.FractalService;
import net.unstream.fractal.api.domain.Fractal;
import net.unstream.fractal.api.domain.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FractalServiceImpl implements FractalService {

	private final FractalRepository fractalRepository;
	private final ImageRepository imageRepository;

	@Autowired
	public FractalServiceImpl(FractalRepository fractalRepository, ImageRepository imageRepository) {
		this.fractalRepository = fractalRepository;
		this.imageRepository = imageRepository;
	}

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
	public Fractal findById(String id) {
		return fractalRepository.findById(id).get();
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#findImageById(long)
	 */
    @Override
	@Transactional(readOnly=true)
	public Image findImageById(String id) {
		return imageRepository.findById(id).get();
	}

    /* (non-Javadoc)
	 * @see net.unstream.fractal.db.FractalService#delete(java.lang.Long)
	 */
    @Override
	@Transactional
	public void delete(String id) {
       	fractalRepository.deleteById(id);
	}

}
