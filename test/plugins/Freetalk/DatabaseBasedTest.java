/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package plugins.Freetalk;

import static java.lang.Math.abs;

import java.io.File;

import junit.framework.TestCase;
import java.util.Random;

import com.db4o.ext.ExtObjectContainer;

import freenet.crypt.DummyRandomSource;

/**
 * A JUnit <code>TestCase</code> which opens a db4o database in setUp() and closes it in tearDown().
 * The filename of the database is chosen as the name of the test function currently run plus a
 * random number, and the file is deleted after the database has been closed.
 * When setting up the test, it is assured that the database file does not exist.
 * 
 * The database can be accessed through the member variable <code>db</code>.
 * 
 * You have to call super.setUp() and super.tearDown() if you override one of those methods.
 * 
 * @author xor
 */
public class DatabaseBasedTest extends TestCase {
	
	protected Freetalk mFreetalk;

	private String mDatabaseFilename;

	/**
	 * The database used by this test.
	 */
	protected ExtObjectContainer db;

	/** A weak PRNG the seed of which will be shown on stdout so you can reproduce failing tests. */
	protected DummyRandomSource mRandom;

	/**
	 * @return Returns the filename of the database. This is the name of the current test function plus ".db4o".
	 */
	public String getDatabaseFilename() {
		return mDatabaseFilename;
	}

	/**
	 * You have to call super.setUp() if you override this method.
	 */
	@Override protected void setUp() throws Exception {
		super.setUp();
		
		Long seed = null; // Insert a seed here to re-run with it.
		mRandom = new DummyRandomSource(seed != null ? seed : (seed = new Random().nextLong()));
		System.out.println(this + " Random seed: " + seed);
		
		// Use a fresh Random to not obey the seed when generating the filename to ensure re-running
		// with the same seed will not cause collision with the file of the last run.
		mDatabaseFilename = getName() + abs(new Random().nextLong()) + ".db4o";
		
		File databaseFile = new File(getDatabaseFilename());
		if(databaseFile.exists())
			databaseFile.delete();
		assertFalse(databaseFile.exists());
		databaseFile.deleteOnExit(); // Safeguard against tearDown() not being called.

		mFreetalk = new Freetalk(getDatabaseFilename()); 
		db = mFreetalk.getDatabase();
	}

	/**
	 * You have to call super.tearDown() if you override this method. 
	 */
	@Override protected void tearDown() throws Exception {
		super.tearDown();
		
		db.close();
		db = null;
		new File(getDatabaseFilename()).delete(); // Also done by Java's deleteOnExit() via setUp()
	}

	/**
	 * Does nothing. Just here because JUnit will complain if there are no tests.
	 */
	public void testSelf() {
		
	}

}
