package org.example;

import org.hipparchus.util.FastMath;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

public class keplerorbit {
    public static void keplerorbit(String[] args){
        double sma = 7000e3; // Semi-major axis [m]
        double ecc = 0.001; // Eccentricity [-]
        double inc = FastMath.toRadians(15); // Inclination [rad]
        double pa = FastMath.toRadians(30); // Perigee Argument [rad]
        double raan = FastMath.toRadians(45); // Right Ascension of the Ascending Node[rad]
        double anomaly = FastMath.toRadians(60); // Anomaly [rad]

        PositionAngleType positionAngleType = PositionAngleType.MEAN; // Type of anomaly angle used (MEAN, TRUE, ECCENTRIC)
        Frame inertialFrame = FramesFactory.getGCRF(); // Earth-Centered Inertial frame
        AbsoluteDate date = new AbsoluteDate(2026, 7, 23, 3, 26, 0, TimeScalesFactory.getUTC()); // Date of the orbit
        double mu = Constants.EIGEN5C_EARTH_MU; // Earth's standard gravitational parameter used in EIGEN-5C gravity field model

        Orbit orbit = new KeplerianOrbit(sma, ecc, inc, pa, raan, anomaly,
                positionAngleType, inertialFrame, date, mu);
        System.out.println(orbit);

        Propagator propagator = new KeplerianPropagator(orbit);
        AbsoluteDate targetDate = date.shiftedBy(Constants.JULIAN_DAY);
        SpacecraftState propagatedState = propagator.propagate(targetDate);
        System.out.println(propagatedState.getOrbit());
    }
}
