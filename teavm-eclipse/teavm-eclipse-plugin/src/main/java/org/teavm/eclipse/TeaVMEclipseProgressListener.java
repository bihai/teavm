package org.teavm.eclipse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.teavm.vm.TeaVMPhase;
import org.teavm.vm.TeaVMProgressFeedback;
import org.teavm.vm.TeaVMProgressListener;

/**
 *
 * @author Alexey Andreev
 */
class TeaVMEclipseProgressListener implements TeaVMProgressListener {
    private TeaVMProjectBuilder builder;
    private IProgressMonitor progressMonitor;
    private TeaVMPhase currentPhase;
    private int currentProgress;
    private int currentPhaseTotal;
    private int total;
    private int last;

    public TeaVMEclipseProgressListener(TeaVMProjectBuilder builder, IProgressMonitor progressMonitor, int total) {
        this.builder = builder;
        this.progressMonitor = progressMonitor;
        this.total = total;
    }

    @Override
    public TeaVMProgressFeedback phaseStarted(TeaVMPhase phase, int count) {
        if (phase != currentPhase) {
            String taskName = "Building";
            switch (phase) {
                case DECOMPILATION:
                    taskName = "Decompiling";
                    break;
                case DEPENDENCY_CHECKING:
                    taskName = "Dependency checking";
                    break;
                case DEVIRTUALIZATION:
                    taskName = "Applying devirtualization";
                    break;
                case LINKING:
                    taskName = "Linking";
                    break;
                case RENDERING:
                    taskName = "Rendering";
                    break;
            }
            progressMonitor.subTask(taskName);
        }
        currentPhase = phase;
        currentProgress = 0;
        currentPhaseTotal = count;
        if (builder.isInterrupted()) {
            progressMonitor.setCanceled(true);
        }
        update();
        return progressMonitor.isCanceled() ? TeaVMProgressFeedback.CANCEL : TeaVMProgressFeedback.CONTINUE;
    }

    @Override
    public TeaVMProgressFeedback progressReached(int progress) {
        currentProgress = progress;
        update();
        if (builder.isInterrupted()) {
            progressMonitor.setCanceled(true);
        }
        return progressMonitor.isCanceled() ? TeaVMProgressFeedback.CANCEL : TeaVMProgressFeedback.CONTINUE;
    }

    private int getActual() {
        if (currentPhase == null) {
            return 0;
        }
        int totalPhases = TeaVMPhase.values().length;
        int min = total * currentPhase.ordinal() / totalPhases;
        if (currentPhaseTotal <= 0) {
            return min;
        }
        int max = total * (currentPhase.ordinal() + 1) / totalPhases - 1;
        return Math.min(max, min + (max - min) * currentProgress / currentPhaseTotal);
    }

    private void update() {
        int actual = getActual();
        if (actual > last) {
            progressMonitor.worked(actual - last);
            last = actual;
        }
    }
}