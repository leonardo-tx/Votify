interface CancelConfirmationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
}

export default function CancelConfirmationModal({
                                                    isOpen,
                                                    onClose,
                                                    onConfirm,
                                                }: CancelConfirmationModalProps) {
    if (!isOpen) return null;

    return (
        <div
            role="dialog"
            aria-modal="true"
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
        >
            <div className="bg-white rounded p-6 shadow-lg w-[90%] max-w-md text-center">
                <h2 className="text-lg font-bold mb-4 text-gray-800">Confirmar cancelamento</h2>
                <p className="mb-6 text-gray-700">
                    Tem certeza que deseja cancelar esta enquete? Esta ação não pode ser desfeita.
                </p>
                <div className="flex justify-center gap-4">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-100 text-gray-900 rounded hover:bg-gray-200 border border-gray-400"
                    >
                        Voltar
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 font-semibold shadow-md"
                    >
                        Confirmar Cancelamento
                    </button>
                </div>
            </div>
        </div>
    );
}
