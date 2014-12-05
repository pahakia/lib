package pahakia.fault;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockWorker<T> {
    private CodeBlock<T> codeBlock;
    private List<KatchHandler<T>> handlers = new ArrayList<>();
    private FinaleBlock finaleBlock;

    CodeBlockWorker(CodeBlock<T> codeBlock) {
        this.codeBlock = codeBlock;
    }

    public CodeBlockWorker<T> ignore(String code0, String... codes) {
        handlers.add(new KatchHandler<T>(code0, codes));
        return this;
    }

    public CodeBlockWorker<T> ignore(FaultCode code0, FaultCode... codes) {
        String[] strCodes = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            FaultCode code = codes[i];
            strCodes[i] = code.getCode();
        }
        handlers.add(new KatchHandler<T>(code0.getCode(), strCodes));
        return this;
    }

    public CodeBlockWorker<T> ignore(Class<? extends Throwable> clazz,
            @SuppressWarnings("unchecked") Class<? extends Throwable>... classes) {
        String[] codes = new String[classes.length];
        for (int i = 0; i < codes.length; i++) {
            Class<? extends Throwable> clz = classes[i];
            codes[i] = clz.getName();
        }
        handlers.add(new KatchHandler<T>(clazz.getName(), codes));
        return this;
    }

    public CodeBlockWorker<T> katch(KatchBlock<T> katchBlock, String code0, String... codes) {
        handlers.add(new KatchHandler<T>(katchBlock, code0, codes));
        return this;
    }

    public CodeBlockWorker<T> katch(String code, KatchBlock<T> katchBlock) {
        return katch(katchBlock, code);
    }

    public CodeBlockWorker<T> katch(KatchBlock<T> katchBlock, FaultCode code0, FaultCode... codes) {
        String[] strCodes = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            FaultCode code = codes[i];
            strCodes[i] = code.getCode();
        }
        handlers.add(new KatchHandler<T>(katchBlock, code0.getCode(), strCodes));
        return this;
    }

    public CodeBlockWorker<T> katch(FaultCode code, KatchBlock<T> katchBlock) {
        return katch(katchBlock, code);
    }

    public CodeBlockWorker<T> katch(Class<? extends Throwable> clz, KatchBlock<T> katchBlock) {
        handlers.add(new KatchHandler<T>(katchBlock, clz.getName()));
        return this;
    }

    public CodeBlockWorker<T> katch(KatchBlock<T> katchBlock, Class<? extends Throwable> clazz,
            @SuppressWarnings("unchecked") Class<? extends Throwable>... classes) {
        String[] codes = new String[classes.length];
        for (int i = 0; i < codes.length; i++) {
            Class<? extends Throwable> clz = classes[i];
            codes[i] = clz.getName();
        }
        handlers.add(new KatchHandler<T>(katchBlock, clazz.getName(), codes));
        return this;
    }

    public CodeBlockWorker<T> katchAll(KatchBlock<T> katchBlock) {
        handlers.add(new KatchHandler<T>(katchBlock, null));
        return this;
    }

    public T finale(FinaleBlock finaleBlock) {
        this.finaleBlock = finaleBlock;
        return finale();
    }

    public T finale() {
        try {
            return codeBlock.f();
        } catch (Throwable t) {
            Fault ex = Fault.naturalize(t);
            for (KatchHandler<T> handler : handlers) {
                if (handler.code0 == null || ex.getCode().getCode().matches(handler.code0)) {
                    if (handler.katchBlock != null) {
                        return handler.katchBlock.f(ex);
                    } else {
                        return null;
                    }
                }
                for (String code : handler.codes) {
                    if (ex.getCode().getCode().matches(code)) {
                        return handler.katchBlock.f(ex);
                    }
                }
            }
            throw ex;
        } finally {
            if (finaleBlock != null) {
                finaleBlock.f();
            }
        }
    }
}
