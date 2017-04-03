package visan.statistics;

/**
 *
 * @author Ramzan
 */
public class chi_table {

    public static double check(int df) {
        switch (df) {
            case 1:
                return 3.84;
            case 2:
                return 5.99;
            case 3:
                return 7.82;
            case 4:
                return 9.49;
            case 5:
                return 11.07;
            case 6:
                return 12.59;
            case 7:
                return 14.07;
            case 8:
                return 15.51;
            case 9:
                return 16.92;
            case 10:
                return 18.31;
            case 11:
                return 19.68;
            case 12:
                return 21.03;
            case 13:
                return 22.36;
            case 14:
                return 23.69;
            case 15:
                return 25.00;
            case 16:
                return 26.30;
            case 17:
                return 27.59;
            case 18:
                return 28.87;
            case 19:
                return 30.14;
            case 20:
                return 31.41;
            case 21:
                return 32.67;
            case 22:
                return 33.92;
            case 23:
                return 35.17;
            case 24:
                return 36.42;
            case 25:
                return 37.65;
            case 26:
                return 38.89;
            case 27:
                return 40.11;
            case 28:
                return 41.34;
            case 29:
                return 42.56;
            case 30:
                return 43.77;
            case 31:
                return 44.99;
            case 32:
                return 46.19;
            case 33:
                return 47.40;
            case 34:
                return 48.60;
            case 35:
                return 49.80;
            case 36:
                return 51.00;
            case 37:
                return 52.19;
            case 38:
                return 53.38;
            case 39:
                return 54.57;
            case 40:
                return 55.76;
        }
        return 0;
    }
}
