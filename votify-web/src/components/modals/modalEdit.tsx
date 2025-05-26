import React, { useState, Fragment } from "react";
import { Dialog, Transition } from "@headlessui/react";
import Input from "@/components/shared/Input";
import Button from "@/components/shared/Button";

interface ModalEditProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (data: {
        title: string;
        description: string;
        startDate: string;
        endDate: string;
    }) => void;
    initialData: {
        title: string;
        description: string;
        startDate: string;
        endDate: string;
    };
    now: string;
}

const ModalEdit: React.FC<ModalEditProps> = ({
    isOpen,
    onClose,
    onSave,
    initialData,
    now,
}) => {
    const [formData, setFormData] = useState({
        ...initialData,
        startDate: (initialData.startDate.substring(0, 16)),
        endDate: (initialData.endDate.substring(0, 16)),
    });

    const hasStarted = new Date(now) >= new Date(formData.startDate);

    const handleSave = () => {
        const endDate = new Date(formData.endDate);
        if (endDate <= new Date(now)) {
            alert("A data de término deve ser maior que o horário atual.");
            return;
        }
        onSave({
            ...formData,
            startDate: new Date(formData.startDate).toISOString(),
            endDate: new Date(formData.endDate).toISOString(),
        });
        onClose();
    };

    return (
        <Transition appear show={isOpen} as={Fragment}>
            <Dialog as="div" className="relative z-50" onClose={onClose}>
                <Transition.Child
                    as={Fragment}
                    enter="ease-out duration-200"
                    enterFrom="opacity-0"
                    enterTo="opacity-100"
                    leave="ease-in duration-150"
                    leaveFrom="opacity-100"
                    leaveTo="opacity-0"
                >
                    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm" />
                </Transition.Child>

                <div className="fixed inset-0 flex items-center justify-center p-4">
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-200"
                        enterFrom="opacity-0 scale-95"
                        enterTo="opacity-100 scale-100"
                        leave="ease-in duration-150"
                        leaveFrom="opacity-100 scale-100"
                        leaveTo="opacity-0 scale-95"
                    >
                        <Dialog.Panel className="w-full max-w-xl p-8 rounded-2xl shadow-lg bg-zinc-900 text-white">
                            <Dialog.Title className="text-3xl font-bold text-center">
                                Editar Enquete
                            </Dialog.Title>
                            <p className="mt-2 text-center text-gray-400">
                                {hasStarted
                                    ? "Apenas a data de término pode ser alterada."
                                    : "Atualize os detalhes da enquete."}
                            </p>

                            <form className="space-y-6 mt-6">
                                <div className="space-y-4">
                                    <Input
                                        id="edit-title"
                                        type="text"
                                        required
                                        placeholder="Título"
                                        className="w-full bg-zinc-800 text-white placeholder-gray-400 border border-zinc-700"
                                        value={formData.title}
                                        disabled={hasStarted}
                                        onChange={(e) =>
                                            setFormData((prev) => ({ ...prev, title: e.target.value }))
                                        }
                                    />

                                    <textarea
                                        id="edit-description"
                                        required
                                        placeholder="Descrição"
                                        className="w-full p-3 bg-zinc-800 text-white placeholder-gray-400 border border-zinc-700 rounded-md resize-y"
                                        rows={4}
                                        value={formData.description}
                                        disabled={hasStarted}
                                        onChange={(e) =>
                                            setFormData((prev) => ({
                                                ...prev,
                                                description: e.target.value,
                                            }))
                                        }
                                    />

                                    <Input
                                        id="edit-start-date"
                                        type="datetime-local"
                                        required
                                        placeholder="Data de Início"
                                        className="w-full bg-zinc-800 text-white border border-zinc-700"
                                        value={formData.startDate}
                                        disabled={hasStarted}
                                        onChange={(e) =>
                                            setFormData((prev) => ({
                                                ...prev,
                                                startDate: e.target.value,
                                            }))
                                        }
                                    />

                                    <Input
                                        id="edit-end-date"
                                        type="datetime-local"
                                        required
                                        placeholder="Data de Término"
                                        className="w-full bg-zinc-800 text-white border border-zinc-700"
                                        value={formData.endDate}
                                        onChange={(e) =>
                                            setFormData((prev) => ({
                                                ...prev,
                                                endDate: e.target.value,
                                            }))
                                        }
                                    />
                                </div>

                                <div className="flex justify-between mt-6">
                                    <Button
                                        type="button"
                                        className="bg-zinc-700 hover:bg-zinc-600 text-white py-2 px-4 rounded-md"
                                        onClick={onClose}
                                    >
                                        Cancelar
                                    </Button>
                                    <Button
                                        type="button"
                                        scheme="primary"
                                        className="bg-indigo-500 hover:bg-indigo-600 text-white py-2 px-4 rounded-md"
                                        onClick={handleSave}
                                    >
                                        Salvar
                                    </Button>
                                </div>
                            </form>
                        </Dialog.Panel>
                    </Transition.Child>
                </div>
            </Dialog>
        </Transition>
    );
};

export default ModalEdit;
