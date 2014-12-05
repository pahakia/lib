package pahakia.fault;

class KatchHandler<T> {
    String code0;
    String[] codes;
    KatchBlock<T> katchBlock;

    KatchHandler(String code0, String... codes) {
        this.code0 = code0;
        this.codes = codes;
    }

    KatchHandler(KatchBlock<T> katchBlock, String code0, String... codes) {
        this(code0, codes);
        this.katchBlock = katchBlock;
    }
}